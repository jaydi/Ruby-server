package com.jaydi.ruby.apis;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.jaydi.ruby.constants.Constants;
import com.jaydi.ruby.models.BaseModel;
import com.jaydi.ruby.models.Gem;
import com.jaydi.ruby.models.GemCol;
import com.jaydi.ruby.models.PMF;
import com.jaydi.ruby.models.Rubymine;

@Api(name = "rubymine", version = "v1", clientIds = { Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID, Constants.WEB_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID })
public class Gems {

	@ApiMethod(name = "gems.insert", path = "gem", httpMethod = "post")
	public Gem insert(Gem gem) {
		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(gem);
		pm.close();
		return gem;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "gems.list.mine", path = "gems/mine", httpMethod = "get")
	public GemCol listMine(@Named("rubymineId") long rubymineId) {
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(Gem.class);
		List<Gem> cans = (List<Gem>) pm.newQuery(query).execute();

		List<Gem> gems = new ArrayList<Gem>();
		for (Gem can : cans)
			if (can.getRubymineId().equals(rubymineId))
				gems.add(can);

		GemCol gemCol = new GemCol();
		gemCol.setGems(gems);

		return gemCol;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "gems.list.zone", path = "gems/zone", httpMethod = "get")
	public GemCol listZone(@Named("rubyzoneId") long rubyzoneId) {
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(Gem.class);
		List<Gem> cans = (List<Gem>) pm.newQuery(query).execute();
		query = pm.newQuery(Rubymine.class);
		List<Rubymine> canmines = (List<Rubymine>) pm.newQuery(query).execute();

		List<Rubymine> rubymines = new ArrayList<Rubymine>();
		for (Rubymine canmine : canmines)
			if (canmine.getRubyzoneId().equals(rubyzoneId))
				rubymines.add(canmine);

		List<Gem> gems = new ArrayList<Gem>();
		for (Gem can : cans)
			for (Rubymine rubymine : rubymines)
				if (can.getRubymineId().equals(rubymine.getId())) {
					gems.add(can);
					break;
				}

		GemCol gemCol = new GemCol();
		gemCol.setGems(gems);

		return gemCol;
	}

	@ApiMethod(name = "gems.edit", path = "gem", httpMethod = "put")
	public Gem edit(Gem gem) {
		PersistenceManager pm = PMF.getPersistenceManager();
		try {
			Gem target = pm.getObjectById(Gem.class, gem.getId());
			target.update(gem);
			pm.close();

			return target;

		} catch (JDOObjectNotFoundException e) {
			Gem target = new Gem();
			target.setResultCode(BaseModel.NO_DATA);
			return target;
		}
	}

	@ApiMethod(name = "gems.delete", path = "gem", httpMethod = "delete")
	public void delete(@Named("id") long id) {
		PersistenceManager pm = PMF.getPersistenceManager();
		try {
			Gem target = pm.getObjectById(Gem.class, id);
			pm.deletePersistent(target);

		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "gems.aall", path = "gem/all", httpMethod = "get")
	public GemCol all() {
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(Gem.class);
		List<Gem> gems = (List<Gem>) pm.newQuery(query).execute();
		return new GemCol(gems);
	}

}
