package com.jaydi.ruby.apis.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jaydi.ruby.gcm.GcmManager;
import com.jaydi.ruby.models.Event;
import com.jaydi.ruby.models.PMF;
import com.jaydi.ruby.models.Ruby;
import com.jaydi.ruby.models.User;

public class CheckVisitEventTask extends HttpServlet {
	private static final long serialVersionUID = 8390136632975300834L;

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long userId = Long.valueOf(request.getParameter("userId"));
		long targetId = Long.valueOf(request.getParameter("targetId"));
		int type = Integer.valueOf(request.getParameter("type"));
		String targetName = request.getParameter("targetName");

		PersistenceManager pm = PMF.getPersistenceManager();

		User user = pm.getObjectById(User.class, userId);
		Query query = pm.newQuery(Event.class);
		query.setFilter("userId == theUserId && targetId == theTargetId && type == theType");
		query.declareParameters("long theUserId, long theTargetId, int theType");
		List<Event> events = (List<Event>) pm.newQuery(query).execute(userId, targetId, type);

		if (events.isEmpty())
			return;

		Event event = events.get(0);
		event.setMessage("미션 클리어! " + targetName + " 방문 보너스 루비 10개를 받았습니다!");
		sendEvent(event);

		Ruby ruby = new Ruby();
		ruby.setUserId(userId);
		ruby.setPlanterId(targetId);
		ruby.setGiverId(0l);
		ruby.setValue(10f);
		ruby.setCreatedAt(new Date().getTime());
		ruby.setEvent(Ruby.EVENT_AD_FOLLOW);

		user.setRuby(user.getRuby() + event.getRuby());
		pm.deletePersistent(event);

		pm.close();
	}

	private void sendEvent(Event event) {
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(event.getUserId());
		GcmManager.sendEventClear(userIds, event);
	}

}
