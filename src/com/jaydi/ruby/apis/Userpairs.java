package com.jaydi.ruby.apis;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
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
import com.jaydi.ruby.models.PMF;
import com.jaydi.ruby.models.User;
import com.jaydi.ruby.models.Userpair;
import com.jaydi.ruby.models.UserpairCol;

@Api(name = "rubymine", version = "v1", clientIds = { Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID, Constants.WEB_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID })
public class Userpairs {

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "userpairs.list", path = "userpairs", httpMethod = "get")
	public UserpairCol list(@Named("userId") long userId) {
		PersistenceManager pm = PMF.getPersistenceManager();

		Query query = pm.newQuery(Userpair.class);
		query.setFilter("userIdA == id");
		query.declareParameters("long id");
		List<Userpair> userpairsA = (List<Userpair>) pm.newQuery(query).execute(userId);

		query.setFilter("userIdB == id");
		query.declareParameters("long id");
		List<Userpair> userpairsB = (List<Userpair>) pm.newQuery(query).execute(userId);

		List<Userpair> userpairs = new ArrayList<Userpair>();
		userpairs.addAll(userpairsA);
		userpairs.addAll(userpairsB);

		UserpairCol userpairCol = new UserpairCol();
		userpairCol.setUserpairs(userpairs);

		return userpairCol;
	}

	@ApiMethod(name = "userpairs.update", path = "userpair", httpMethod = "put")
	public Userpair update(Userpair userpair) {
		PersistenceManager pm = PMF.getPersistenceManager();
		Userpair target = null;

		try {
			target = pm.getObjectById(Userpair.class, userpair.getId());
			target.update(userpair);

			updateUserLevelTask(target.getUserIdA());
			updateUserLevelTask(target.getUserIdB());

		} catch (JDOObjectNotFoundException e) {
			target = new Userpair();
			target.setResultCode(BaseModel.NO_DATA);
		} finally {
			pm.close();
		}

		return target;
	}

	@ApiMethod(name = "userpairs.delete", path = "userpair", httpMethod = "delete")
	public void delete(@Named("id") long id) {
		PersistenceManager pm = PMF.getPersistenceManager();
		Userpair target = null;

		try {
			target = pm.getObjectById(Userpair.class, id);
			Userpair dup = new Userpair(target);
			pm.deletePersistent(target);

			updateUserLevelTask(dup.getUserIdA());
			updateUserLevelTask(dup.getUserIdB());

		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "userpairs.pair", path = "userpair/pair", httpMethod = "post")
	public void pair(Userpair userpair) {
		if (userpair.getUserIdA().equals(userpair.getUserIdB()))
			return;

		PersistenceManager pm = PMF.getPersistenceManager();

		try {
			pm.getObjectById(User.class, userpair.getUserIdA());
			pm.getObjectById(User.class, userpair.getUserIdB());

			Query query = pm.newQuery(Userpair.class);
			query.setFilter("userIdA == idA && userIdB == idB");
			query.declareParameters("long idA, long idB");
			List<Userpair> userpairsA = (List<Userpair>) pm.newQuery(query).execute(userpair.getUserIdA(), userpair.getUserIdB());

			query.setFilter("userIdA == idB && userIdB == idA");
			query.declareParameters("long idA, long idB");
			List<Userpair> userpairsB = (List<Userpair>) pm.newQuery(query).execute(userpair.getUserIdA(), userpair.getUserIdB());

			List<Userpair> userpairs = new ArrayList<Userpair>();
			userpairs.addAll(userpairsA);
			userpairs.addAll(userpairsB);

			if (!userpairs.isEmpty())
				return;

			pm.makePersistent(userpair);
			pm.close();

			updateUserLevelTask(userpair.getUserIdA());
			updateUserLevelTask(userpair.getUserIdB());

		} catch (JDOObjectNotFoundException e) {
		}
	}

	private void updateUserLevelTask(Long userId) {
		Queue queue = QueueFactory.getDefaultQueue();
		TaskOptions opt = TaskOptions.Builder.withUrl("/update_user_level_task");
		opt.param("userId", userId.toString());
		queue.add(opt);
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "userpairs.all", path = "userpair/all", httpMethod = "get")
	public UserpairCol all() {
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(Userpair.class);
		List<Userpair> userpairs = (List<Userpair>) pm.newQuery(query).execute();
		return new UserpairCol(userpairs);
	}

}
