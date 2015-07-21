package com.jaydi.ruby.apis.tasks;

import java.io.IOException;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jaydi.ruby.models.PMF;
import com.jaydi.ruby.models.User;
import com.jaydi.ruby.models.Userpair;

public class UpdateUserInfoTask extends HttpServlet {
	private static final long serialVersionUID = -4068485253917376409L;

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long userId = Long.valueOf(request.getParameter("userId"));

		PersistenceManager pm = PMF.getPersistenceManager();
		try {
			User user = pm.getObjectById(User.class, userId);

			Query query = pm.newQuery(Userpair.class);
			query.setFilter("userIdA == id");
			query.declareParameters("long id");
			List<Userpair> userpairsA = (List<Userpair>) pm.newQuery(query).execute(user.getId());

			for (Userpair userpairA : userpairsA) {
				userpairA.setUserImageKeyA(user.getImageKey());
				userpairA.setUserNameA(user.getName());
			}

			query.setFilter("userIdB == id");
			query.declareParameters("long id");
			List<Userpair> userpairsB = (List<Userpair>) pm.newQuery(query).execute(user.getId());

			for (Userpair userpairB : userpairsB) {
				userpairB.setUserImageKeyB(user.getImageKey());
				userpairB.setUserNameB(user.getName());
			}

			pm.close();

		} catch (JDOObjectNotFoundException e) {
		}
	}

}
