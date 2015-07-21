package com.jaydi.ruby.apis.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jaydi.ruby.gcm.GcmManager;
import com.jaydi.ruby.models.Event;
import com.jaydi.ruby.models.PMF;
import com.jaydi.ruby.models.Ruby;
import com.jaydi.ruby.models.User;

public class SendEventTask extends HttpServlet {
	private static final long serialVersionUID = 8390136632970834L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long userId = Long.valueOf(request.getParameter("userId"));
		long targetId = Long.valueOf(request.getParameter("targetId"));
		int type = Integer.valueOf(request.getParameter("type"));
		float rubyValue = Float.valueOf(request.getParameter("rubyValue"));
		String msg = request.getParameter("msg");
		boolean clear = Boolean.valueOf(request.getParameter("clear"));
		int rubyEvent = Integer.valueOf(request.getParameter("rubyEvent"));

		Event event = new Event();
		event.setId(0l);
		event.setUserId(userId);
		event.setTargetId(targetId);
		event.setType(type);
		event.setRuby(rubyValue);
		event.setMessage(msg);

		List<Long> userIds = new ArrayList<Long>();
		userIds.add(event.getUserId());
		if (clear)
			GcmManager.sendEventClear(userIds, event);
		else
			GcmManager.sendEventNotice(userIds, event);

		Ruby ruby = new Ruby();
		ruby.setUserId(userId);
		ruby.setPlanterId(targetId);
		ruby.setGiverId(0l);
		ruby.setValue(rubyValue);
		ruby.setCreatedAt(new Date().getTime());
		ruby.setEvent(rubyEvent);

		PersistenceManager pm = PMF.getPersistenceManager();
		User user = pm.getObjectById(User.class, userId);
		user.setRuby(user.getRuby() + rubyValue);
		pm.makePersistent(ruby);
		pm.close();
	}

}
