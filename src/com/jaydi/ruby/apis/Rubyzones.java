package com.jaydi.ruby.apis;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.jaydi.ruby.constants.Constants;
import com.jaydi.ruby.models.PMF;
import com.jaydi.ruby.models.Rubyzone;
import com.jaydi.ruby.models.RubyzoneCol;

@Api(name = "rubymine", version = "v1", clientIds = { Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID, Constants.WEB_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID })
public class Rubyzones {

	@ApiMethod(name = "rubyzones.insert", path = "rubyzone", httpMethod = "post")
	public Rubyzone insert(Rubyzone rubyzone) {
		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(rubyzone);
		pm.close();
		return rubyzone;
	}

	// redundancy
	@SuppressWarnings("unchecked")
	@ApiMethod(name = "rubyzones.list", path = "rubyzones", httpMethod = "get")
	public RubyzoneCol list() {
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(Rubyzone.class);
		List<Rubyzone> rubyzones = (List<Rubyzone>) pm.newQuery(query).execute();

		RubyzoneCol rubyzoneCol = new RubyzoneCol();
		rubyzoneCol.setRubyzones(rubyzones);

		return rubyzoneCol;
	}

	// redundancy
	@SuppressWarnings("unchecked")
	@ApiMethod(name = "rubyzones.all", path = "rubyzone/all", httpMethod = "get")
	public RubyzoneCol all() {
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(Rubyzone.class);
		List<Rubyzone> rubyzones = (List<Rubyzone>) pm.newQuery(query).execute();
		return new RubyzoneCol(rubyzones);
	}

}
