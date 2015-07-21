package com.jaydi.ruby.apis.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jaydi.ruby.gcm.GcmManager;
import com.jaydi.ruby.models.PMF;
import com.jaydi.ruby.models.User;
import com.jaydi.ruby.models.Userpair;

public class UpdateUserLevelTask extends HttpServlet {
	private static final long serialVersionUID = 8390136632975300834L;

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long userId = Long.valueOf(request.getParameter("userId"));

		PersistenceManager pm = PMF.getPersistenceManager();
		try {
			User user = pm.getObjectById(User.class, userId);

			Query query = pm.newQuery(Userpair.class);
			query.setFilter("state == alive && userIdA == id");
			query.declareParameters("int alive, long id");
			List<Userpair> userpairsA = (List<Userpair>) pm.newQuery(query).execute(Userpair.STATE_ALIVE, user.getId());

			query.setFilter("state == alive && userIdB == id");
			query.declareParameters("int alive, long id");
			List<Userpair> userpairsB = (List<Userpair>) pm.newQuery(query).execute(Userpair.STATE_ALIVE, user.getId());

			List<Userpair> userpairs = new ArrayList<Userpair>();
			userpairs.addAll(userpairsA);
			userpairs.addAll(userpairsB);

			int newLevel = User.calLevel(userpairs.size());
			if (user.getLevel() < newLevel)
				sendLevelChagne(userId, true);
			else if (user.getLevel() > newLevel)
				sendLevelChagne(userId, false);
			user.setLevel(newLevel);

		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
	}

	private void sendLevelChagne(long userId, boolean change) {
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(userId);
		GcmManager.sendLevelChange(userIds, change);
	}

}
