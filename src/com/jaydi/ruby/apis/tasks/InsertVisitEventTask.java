package com.jaydi.ruby.apis.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jaydi.ruby.gcm.GcmManager;
import com.jaydi.ruby.models.Event;
import com.jaydi.ruby.models.Job;
import com.jaydi.ruby.models.PMF;

public class InsertVisitEventTask extends HttpServlet {
	private static final long serialVersionUID = 8390136632975300834L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long userId = Long.valueOf(request.getParameter("userId"));
		long targetId = Long.valueOf(request.getParameter("targetId"));
		String message = request.getParameter("message");
		int type = Integer.valueOf(request.getParameter("type"));
		float ruby = Float.valueOf(request.getParameter("ruby"));

		Event event = new Event();
		event.setUserId(userId);
		event.setTargetId(targetId);
		event.setMessage(message);
		event.setType(type);
		event.setRuby(ruby);

		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(event);

		Job job = new Job();
		job.setType(Job.TYPE_ERASE_EVENT);
		job.setTargetId(event.getId());
		job.setScheduledTime(getMidnightTime());

		pm.makePersistent(job);
		pm.close();

		sendEvent(event);
	}

	private long getMidnightTime() {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		return c.getTimeInMillis();
	}

	private void sendEvent(Event event) {
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(event.getUserId());
		GcmManager.sendEventNotice(userIds, event);
	}

}
