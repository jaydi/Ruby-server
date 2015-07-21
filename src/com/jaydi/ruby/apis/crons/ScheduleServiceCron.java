package com.jaydi.ruby.apis.crons;

import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.jaydi.ruby.models.Coupon;
import com.jaydi.ruby.models.Event;
import com.jaydi.ruby.models.Job;
import com.jaydi.ruby.models.PMF;
import com.jaydi.ruby.models.Ruby;
import com.jaydi.ruby.models.User;
import com.jaydi.ruby.utils.TimeUtils;

public class ScheduleServiceCron extends HttpServlet {
	private static final long serialVersionUID = -537021941454649111L;

	@SuppressWarnings("unchecked")
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) {
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(Job.class);
		query.setFilter("scheduledTime < now");
		query.declareParameters("long now");
		List<Job> jobs = (List<Job>) pm.newQuery(query).execute(TimeUtils.getNowTime());

		for (Job job : jobs)
			doJob(pm, job);

		pm.deletePersistentAll(jobs);
		pm.close();
	}

	private void doJob(PersistenceManager pm, Job job) {
		switch (job.getType()) {
		case Job.TYPE_ERASE_PHONE:
			erasePhone(pm, job.getTargetId());
			break;
		case Job.TYPE_ERASE_EVENT:
			eraseEvent(pm, job.getTargetId());
			break;
		case Job.TYPE_SIGNUP_REWARD:
			sendSignupReward(job.getTargetId());
			break;
		case Job.TYPE_EXPIRE_COUPON:
			expireCoupon(pm, job.getTargetId());
			break;
		}
	}

	private void erasePhone(PersistenceManager pm, Long targetId) {
		try {
			User target = pm.getObjectById(User.class, targetId);
			target.setPhone("");

		} catch (JDOObjectNotFoundException e) {
		}
	}

	private void eraseEvent(PersistenceManager pm, Long targetId) {
		try {
			Event target = pm.getObjectById(Event.class, targetId);
			pm.deletePersistent(target);

		} catch (JDOObjectNotFoundException e) {
		}
	}

	private void sendSignupReward(Long userId) {
		Queue queue = QueueFactory.getDefaultQueue();
		TaskOptions opt = TaskOptions.Builder.withUrl("/send_event_task");
		opt.param("userId", "" + userId);
		opt.param("targetId", "0");
		opt.param("type", "" + Event.TYPE_SIGN_UP);
		opt.param("rubyValue", "" + 20);
		opt.param("rubyEvent", "" + Ruby.EVENT_SIGN_UP);
		opt.param("msg", "가입 축하 이벤트! 보너스 루비 20개를 받았습니다!\n\n메가박스 팝콘 구매가능합니다.");
		opt.param("clear", "true");
		queue.add(opt);
	}

	private void expireCoupon(PersistenceManager pm, Long targetId) {
		try {
			Coupon target = pm.getObjectById(Coupon.class, targetId);
			target.setState(Coupon.STATE_EXPIRED);

		} catch (JDOObjectNotFoundException e) {
		}
	}
}
