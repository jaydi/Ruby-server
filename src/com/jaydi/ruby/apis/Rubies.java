package com.jaydi.ruby.apis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.jaydi.ruby.constants.Constants;
import com.jaydi.ruby.models.BaseModel;
import com.jaydi.ruby.models.Event;
import com.jaydi.ruby.models.PMF;
import com.jaydi.ruby.models.Ruby;
import com.jaydi.ruby.models.RubyCol;
import com.jaydi.ruby.models.Rubymine;
import com.jaydi.ruby.models.Rubyzone;
import com.jaydi.ruby.models.User;
import com.jaydi.ruby.utils.Algorithm;
import com.jaydi.ruby.utils.TimeUtils;

@Api(name = "rubymine", version = "v1", clientIds = { Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID, Constants.WEB_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID })
public class Rubies {
	private static final Logger log = Logger.getLogger(Rubies.class.getName());

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "rubies.insert", path = "ruby", httpMethod = "post")
	public Ruby insert(Ruby ruby) {
		PersistenceManager pm = PMF.getPersistenceManager();

		Query query = pm.newQuery(Ruby.class);
		query.setFilter("planterId == planter && giverId == giver");
		query.declareParameters("long planter, long giver");
		List<Ruby> rubies = (List<Ruby>) pm.newQuery(query).execute(ruby.getPlanterId(), ruby.getGiverId());

		if (rubies.isEmpty())
			pm.makePersistent(ruby);
		else
			rubies.get(0).setValue(rubies.get(0).getValue() + ruby.getValue());

		try {
			Rubymine planter = pm.getObjectById(Rubymine.class, ruby.getPlanterId());
			planter.setRuby(ruby.getValue());
			pm.close();
			return ruby;

		} catch (JDOObjectNotFoundException e) {
			ruby.setResultCode(BaseModel.NO_DATA);
			return ruby;
		}
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "rubies.mine", path = "ruby/mine", httpMethod = "post")
	public RubyCol mine(Ruby ruby) {
		PersistenceManager pm = PMF.getPersistenceManager();
		RubyCol rubyCol = new RubyCol();

		// redundancy check
		Query query = pm.newQuery(Ruby.class);
		query.setFilter("userId == theUserId && planterId == thePlanterId && createdAt > today");
		query.declareParameters("long theUserId, long thePlanterId, long today");
		List<Ruby> rr = (List<Ruby>) pm.newQuery(query).execute(ruby.getUserId(), ruby.getPlanterId(), TimeUtils.getDawnTime());

		if (!rr.isEmpty())
			return null;

		log.info(ruby.getEvent() + "/" + ruby.getUserId() + "/" + ruby.getPlanterId());

		try {
			User user = pm.getObjectById(User.class, ruby.getUserId());
			Rubymine planter;
			if (ruby.getEvent() == Ruby.EVENT_MINE_IN)
				planter = pm.getObjectById(Rubymine.class, ruby.getPlanterId());
			else if (ruby.getEvent() == Ruby.EVENT_ZONE_IN) {
				Rubyzone rubyzone = pm.getObjectById(Rubyzone.class, ruby.getPlanterId());
				planter = new Rubymine();
				planter.setId(ruby.getPlanterId());
				planter.setRubyzoneId(ruby.getPlanterId());
				planter.setName(rubyzone.getName());
			} else
				throw new JDOObjectNotFoundException();

			// set ruby value amplified by user's level factor
			List<Ruby> rubies = new ArrayList<Ruby>();
			float value = 1f * User.calAmpFactor(user.getLevel());
			ruby.setValue(value);
			rubies.add(ruby);

			// get ad givers
			List<Rubymine> givers = Algorithm.getRecommendedRubymines(pm, ruby.getEvent(), planter);

			// prepare result response
			rubyCol.setPlanter(planter);
			rubyCol.setRubies(rubies);
			rubyCol.setGivers(givers);

		} catch (JDOObjectNotFoundException e) {
			rubyCol.setResultCode(BaseModel.NO_DATA);
			return rubyCol;
		}

		return rubyCol;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "rubies.claim", path = "ruby/claim", httpMethod = "post")
	public User claim(RubyCol rubyCol) {
		Ruby ruby = rubyCol.getRubies().get(0);
		PersistenceManager pm = PMF.getPersistenceManager();
		User user = null;

		log.info(ruby.getEvent() + "/" + ruby.getUserId() + "/" + ruby.getPlanterId());

		try {
			user = pm.getObjectById(User.class, rubyCol.getRubies().get(0).getUserId());

			Query query = pm.newQuery(Ruby.class);
			query.setFilter("userId == theUserId");
			query.declareParameters("long theUserId");
			List<Ruby> rubies = (List<Ruby>) pm.newQuery(query).execute(user.getId());
			int prc = 0;
			for (Ruby r : rubies)
				if (r.getPlanterId().longValue() > 0l)
					prc++;

			log.info("user : " + user.getId() + " former ruby count : " + prc);

			user.setRuby(user.getRuby() + ruby.getValue());
			ruby.setId(null);
			ruby.setCreatedAt(new Date().getTime());
			pm.makePersistent(ruby);

			if (prc == 0) {
				firstVisitEvent(ruby.getUserId());
				insertVisitEvent(ruby.getUserId(), rubyCol.getGivers().get(0));
			}
			checkVisitEvent(ruby.getUserId(), rubyCol.getPlanter());

			pm.close();

		} catch (JDOObjectNotFoundException e) {
			user = new User();
			user.setId(0l);
			user.setResultCode(BaseModel.NO_DATA);
		} catch (NullPointerException e) {
			user = new User();
			user.setId(0l);
			user.setResultCode(BaseModel.NO_DATA);
		}

		return user;
	}

	private void insertVisitEvent(long userId, Rubymine target) {
		Queue queue = QueueFactory.getDefaultQueue();
		TaskOptions opt = TaskOptions.Builder.withUrl("/insert_visit_event_task");
		opt.param("userId", "" + userId);
		opt.param("targetId", "" + target.getId());
		opt.param("message", "루비 보너스 미션! 오늘 안에 " + target.getName() + " 방문하면 보너스 루비 10개가 쏟아집니다!");
		opt.param("type", "" + Event.TYPE_AD_FOLLOW);
		opt.param("ruby", "" + 10);
		queue.add(opt);
	}

	private void firstVisitEvent(long userId) {
		Queue queue = QueueFactory.getDefaultQueue();
		TaskOptions opt = TaskOptions.Builder.withUrl("/send_event_task");
		opt.param("userId", "" + userId);
		opt.param("targetId", "0");
		opt.param("type", "" + Event.TYPE_AD_FOLLOW);
		opt.param("rubyValue", "" + 10);
		opt.param("rubyEvent", "" + Ruby.EVENT_FIRST_VISIT);
		opt.param("msg", "첫 루비매장 방문을 축하합니다! 보너스 루비 10개를 받았습니다!");
		opt.param("clear", "true");
		queue.add(opt);
	}

	private void checkVisitEvent(long userId, Rubymine target) {
		Queue queue = QueueFactory.getDefaultQueue();
		TaskOptions opt = TaskOptions.Builder.withUrl("/check_visit_event_task");
		opt.param("userId", "" + userId);
		opt.param("targetId", "" + target.getId());
		opt.param("type", "" + Event.TYPE_AD_FOLLOW);
		opt.param("targetName", target.getName());
		queue.add(opt);
	}

	// @ApiMethod(name = "events.push", path = "event", httpMethod = "post")
	// public Event push(Event event) {
	// Queue queue = QueueFactory.getDefaultQueue();
	// TaskOptions opt = TaskOptions.Builder.withUrl("/send_event_task");
	// opt.param("userId", "" + event.getUserId());
	// opt.param("targetId", "" + event.getTargetId());
	// opt.param("type", "" + event.getType());
	// opt.param("rubyValue", "" + event.getRuby());
	// opt.param("msg", event.getMessage());
	// opt.param("clear", "true");
	// queue.add(opt);
	// return event;
	// }

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "rubies.all", path = "ruby/all", httpMethod = "get")
	public RubyCol all() {
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(Ruby.class);
		List<Ruby> rubies = (List<Ruby>) pm.newQuery(query).execute();
		return new RubyCol(rubies);
	}
}