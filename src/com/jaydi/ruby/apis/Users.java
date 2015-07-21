package com.jaydi.ruby.apis;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.inject.Named;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.jaydi.ruby.constants.Constants;
import com.jaydi.ruby.exceptions.DupNameException;
import com.jaydi.ruby.exceptions.DupPhoneException;
import com.jaydi.ruby.exceptions.WrongVerCodeException;
import com.jaydi.ruby.models.BaseModel;
import com.jaydi.ruby.models.Job;
import com.jaydi.ruby.models.PMF;
import com.jaydi.ruby.models.User;
import com.jaydi.ruby.models.UserCol;
import com.jaydi.ruby.models.Userpair;
import com.jaydi.ruby.utils.TimeUtils;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;

@Api(name = "rubymine", version = "v1", clientIds = { Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID, Constants.WEB_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID })
public class Users {
	private static final Logger log = Logger.getLogger(Users.class.getName());

	@ApiMethod(name = "users.insert", path = "user", httpMethod = "post")
	public User insert(User user) {
		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(user);
		pm.close();
		return user;
	}

	@ApiMethod(name = "users.get", path = "user", httpMethod = "get")
	public User get(@Named("id") long id) {
		PersistenceManager pm = PMF.getPersistenceManager();
		User user = null;

		try {
			user = pm.getObjectById(User.class, id);

			log.info("get user: " + user.getName() + "/" + user.getLevel() + "/" + user.getRuby());

		} catch (JDOObjectNotFoundException e) {
			user = new User();
			user.setId(0l);
			user.setResultCode(BaseModel.NO_DATA);
		}

		return user;
	}

	@ApiMethod(name = "users.update", path = "user", httpMethod = "put")
	public User update(User user) {
		PersistenceManager pm = PMF.getPersistenceManager();
		User target = null;

		try {
			target = pm.getObjectById(User.class, user.getId());
			target.update(user);
			updateUserInfoTask(user.getId());

		} catch (JDOObjectNotFoundException e) {
			target = new User();
			target.setId(0l);
			target.setResultCode(BaseModel.NO_DATA);
		} catch (DupNameException e) {
			target.setResultCode(BaseModel.DUP_NAME);
		} catch (DupPhoneException e) {
			target.setResultCode(BaseModel.DUP_PHONE);
		} finally {
			pm.close();
		}

		return target;
	}

	private void updateUserInfoTask(Long id) {
		Queue queue = QueueFactory.getDefaultQueue();
		TaskOptions opt = TaskOptions.Builder.withUrl("/update_user_info_task");
		opt.param("userId", id.toString());
		queue.add(opt);
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "users.start.facebook", path = "user/start/facebook", httpMethod = "post")
	public User startFacebook(User user) {
		PersistenceManager pm = PMF.getPersistenceManager();

		Query query = pm.newQuery(User.class);
		query.setFilter("type == facebook && socialId == facebookId");
		query.declareParameters("int facebook, long facebookId");
		List<User> users = (List<User>) pm.newQuery(query).execute(User.TYPE_FACEBOOK, user.getSocialId());

		if (users.isEmpty()) {
			pm.makePersistent(user);
			pm.close();
			return user;
		} else
			return users.get(0);
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "users.start.kakao", path = "user/start/kakao", httpMethod = "post")
	public User startKakao(User user) {
		PersistenceManager pm = PMF.getPersistenceManager();

		Query query = pm.newQuery(User.class);
		query.setFilter("type == kakao && socialId == kakaoId");
		query.declareParameters("int kakao, long kakaoId");
		List<User> users = (List<User>) pm.newQuery(query).execute(User.TYPE_KAKAO, user.getSocialId());

		if (users.isEmpty()) {
			pm.makePersistent(user);
			pm.close();
			return user;
		} else
			return users.get(0);
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "users.start.email", path = "user/start/email", httpMethod = "post")
	public User startEmail(User user) {
		PersistenceManager pm = PMF.getPersistenceManager();

		Query query = pm.newQuery(User.class);
		query.setFilter("type == typeEmail && email == theEmail && password == thePassword");
		query.declareParameters("int typeEmail, String theEmail, String thePassword");
		List<User> users = (List<User>) pm.newQuery(query).execute(User.TYPE_EMAIL, user.getEmail(), user.getPassword());

		if (users.isEmpty()) {
			user = new User();
			user.setId(0l);
			user.setResultCode(BaseModel.NO_DATA);
			return user;
		} else
			return users.get(0);
	}

	@ApiMethod(name = "users.phone.insert", path = "user/phone/insert", httpMethod = "put")
	public User phoneInsert(User user) {
		PersistenceManager pm = PMF.getPersistenceManager();
		User target = null;

		try {
			target = pm.getObjectById(User.class, user.getId());
			target.setVerCode(sendVerCode(target.getPhone()));

			schedulePhoneDeleteJob(pm, user.getId());

			pm.close();

		} catch (TwilioRestException e) {
			target = new User();
			target.setId(0l);
			target.setResultCode(BaseModel.SEVER_ERROR);
			log.warning(e.getErrorMessage());
		} catch (JDOObjectNotFoundException e) {
			target = new User();
			target.setId(0l);
			target.setResultCode(BaseModel.NO_DATA);
		}

		return target;
	}

	private int sendVerCode(String phone) throws TwilioRestException {
		int verCode = generateVerCode();

		// Build the parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("To", phone));
		params.add(new BasicNameValuePair("From", "+18707768948"));
		params.add(new BasicNameValuePair("Body", "루비 인증번호 : " + verCode));

		TwilioRestClient client = new TwilioRestClient(Constants.TWILLIO_ACCOUNT_SID, Constants.TWILLIO_AUTH_TOKEN);
		MessageFactory messageFactory = client.getAccount().getMessageFactory();
		Message message = messageFactory.create(params);
		log.info("sent phone verification number, sid: " + message.getSid());

		return verCode;
	}

	private int generateVerCode() {
		String randomNumber = String.valueOf(Math.abs(new Random().nextInt()));
		if (randomNumber.length() > 5)
			return Integer.valueOf(randomNumber.substring(0, 5));
		else {
			String verNumber = randomNumber;
			for (int i = 0; i < 5 - randomNumber.length(); i++)
				verNumber = verNumber + "0";
			return Integer.valueOf(verNumber);
		}
	}

	private void schedulePhoneDeleteJob(PersistenceManager pm, Long userId) {
		Job job = new Job();
		job.setType(Job.TYPE_ERASE_PHONE);
		job.setTargetId(userId);
		job.setScheduledTime(TimeUtils.getNowTime() + (1000 * 60 * 3));
		pm.makePersistent(job);
	}

	@ApiMethod(name = "users.phone.verify", path = "user/phone/verify", httpMethod = "put")
	public User phoneVerify(User user) {
		PersistenceManager pm = PMF.getPersistenceManager();
		User target = null;

		try {
			target = pm.getObjectById(User.class, user.getId());
			if (target.getVerCode() != user.getVerCode())
				throw new WrongVerCodeException();

			target.setVerCode(0);
			unschedulePhoneDeleteJob(pm, user.getId());

			pm.close();

		} catch (JDOObjectNotFoundException e) {
			target = new User();
			target.setId(0l);
			target.setResultCode(BaseModel.NO_DATA);
		} catch (WrongVerCodeException e) {
			target = new User();
			target.setId(0l);
			target.setResultCode(BaseModel.WRONG_VER_CODE);
		}

		return target;
	}

	@SuppressWarnings("unchecked")
	private void unschedulePhoneDeleteJob(PersistenceManager pm, Long id) {
		Query query = pm.newQuery(Job.class);
		query.setFilter("type == erasePhone && targetId == userId");
		query.declareParameters("int erasePhone, long userId");
		List<Job> jobs = (List<Job>) pm.newQuery(query).execute(Job.TYPE_ERASE_PHONE, id);
		pm.deletePersistentAll(jobs);
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "users.search", path = "user/search", httpMethod = "get")
	public UserCol search(@Named("id") long id, @Named("name") String name) {
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(User.class);
		query.setFilter("name == theName");
		query.declareParameters("String theName");
		List<User> users = (List<User>) pm.newQuery(query).execute(name);

		query = pm.newQuery(Userpair.class);
		query.setFilter("userIdA == id");
		query.declareParameters("long id");
		List<Userpair> userpairsA = (List<Userpair>) pm.newQuery(query).execute(id);

		query.setFilter("userIdB == id");
		query.declareParameters("long id");
		List<Userpair> userpairsB = (List<Userpair>) pm.newQuery(query).execute(id);

		List<Userpair> userpairs = new ArrayList<Userpair>();
		userpairs.addAll(userpairsA);
		userpairs.addAll(userpairsB);

		for (User user : users)
			for (Userpair userpair : userpairs)
				if (userpair.getUserIdA().equals(user.getId()) || userpair.getUserIdB().equals(user.getId()))
					user.setPaired(true);

		UserCol userCol = new UserCol();
		userCol.setUsers(users);

		return userCol;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "users.all", path = "user/all", httpMethod = "get")
	public UserCol all() {
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(User.class);
		List<User> users = (List<User>) pm.newQuery(query).execute();
		return new UserCol(users);
	}

}
