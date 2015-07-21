package com.jaydi.ruby.apis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Named;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.jaydi.ruby.constants.Constants;
import com.jaydi.ruby.models.BaseModel;
import com.jaydi.ruby.models.PMF;
import com.jaydi.ruby.models.Ruby;
import com.jaydi.ruby.models.Rubymine;
import com.jaydi.ruby.models.RubymineCol;
import com.jaydi.ruby.utils.Algorithm;

@Api(name = "rubymine", version = "v1", clientIds = { Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID, Constants.WEB_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID })
public class Rubymines {

	@ApiMethod(name = "rubymines.insert", path = "rubymine", httpMethod = "post")
	public Rubymine insert(Rubymine rubymine) {
		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(rubymine);
		pm.close();
		return rubymine;
	}

	@ApiMethod(name = "rubymines.get", path = "rubymine", httpMethod = "get")
	public Rubymine get(@Named("id") long id) {
		PersistenceManager pm = PMF.getPersistenceManager();
		Rubymine rubymine = null;

		try {
			rubymine = pm.getObjectById(Rubymine.class, id);

		} catch (JDOObjectNotFoundException e) {
			rubymine = new Rubymine();
			rubymine.setResultCode(BaseModel.NO_DATA);
		}

		return rubymine;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "rubymines.list", path = "rubymines", httpMethod = "get")
	public RubymineCol list(@Named("rubyzoneId") long rubyzoneId) {
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(Rubymine.class);
		query.setFilter("rubyzoneId == theRubyzoneId");
		query.declareParameters("long theRubyzoneId");
		List<Rubymine> results = (List<Rubymine>) pm.newQuery(query).execute(rubyzoneId);

		// shuffle results
		List<Rubymine> rubymines = new ArrayList<Rubymine>(results);
		Collections.shuffle(rubymines);

		// sort by algorithm
		int[] recTypes = Algorithm.getRecTypes(Ruby.EVENT_ZONE_IN, 0);
		List<Rubymine> recs = new ArrayList<Rubymine>();
		List<Rubymine> outs = new ArrayList<Rubymine>(rubymines);
		for (int i = 0; i < recTypes.length; i++)
			if (recTypes[i] > 0)
				for (Rubymine rubymine : rubymines)
					if (rubymine.getType() == recTypes[i]) {
						recs.add(rubymine);
						outs.remove(rubymine);
					}

		recs.addAll(outs);

		RubymineCol rubymineCol = new RubymineCol();
		rubymineCol.setRubymines(recs);

		return rubymineCol;
	}

	@ApiMethod(name = "rubymines.update", path = "rubymine", httpMethod = "put")
	public Rubymine update(Rubymine rubymine) {
		PersistenceManager pm = PMF.getPersistenceManager();
		Rubymine target = null;

		try {
			target = pm.getObjectById(Rubymine.class, rubymine.getId());
			target.update(rubymine);

		} catch (JDOObjectNotFoundException e) {
			target = new Rubymine();
			target.setResultCode(BaseModel.NO_DATA);
		} finally {
			pm.close();
		}

		return target;
	}

	@ApiMethod(name = "rubymines.delete", path = "rubymine", httpMethod = "delete")
	public void delete(@Named("id") long id) {
		PersistenceManager pm = PMF.getPersistenceManager();

		try {
			Rubymine target = pm.getObjectById(Rubymine.class, id);
			pm.deletePersistent(target);
			pm.close();

		} catch (JDOObjectNotFoundException e) {
		}
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "rubymines.all", path = "rubymine/all", httpMethod = "get")
	public RubymineCol all() {
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(Rubymine.class);
		List<Rubymine> rubymines = (List<Rubymine>) pm.newQuery(query).execute();
		return new RubymineCol(rubymines);
	}

}
