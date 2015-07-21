package com.jaydi.ruby.apis;

import java.util.List;

import javax.inject.Named;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.jaydi.ruby.constants.Constants;
import com.jaydi.ruby.models.Log;
import com.jaydi.ruby.models.LogCol;
import com.jaydi.ruby.models.PMF;
import com.jaydi.ruby.models.Receipt;

@Api(name = "rubymine", version = "v1", clientIds = { Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID, Constants.WEB_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID })
public class Logs {

	@ApiMethod(name = "logs.insert", path = "log", httpMethod = "post")
	public void insert(Log log) {
		PersistenceManager pm = PMF.getPersistenceManager();

		if (log.getKind() == Log.KIND_PURCHASE) {
			Receipt receipt = pm.getObjectById(Receipt.class, log.getTargetId());
			log.setRefer(receipt.getShopId());
			log.setCreatedAt(receipt.getCreatedAt());
		}

		pm.makePersistent(log);
		pm.close();
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "logs.list", path = "logs", httpMethod = "get")
	public LogCol list(@Named("after") long after) {
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(Log.class);
		query.setFilter("createdAt > after");
		query.setOrdering("createdAt asc");
		query.declareParameters("long after");
		query.setRange(0, 10000);
		List<Log> logs = (List<Log>) pm.newQuery(query).execute(after);
		return new LogCol(logs);
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "logs.all", path = "log/all", httpMethod = "get")
	public LogCol all() {
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(Log.class);
		List<Log> logs = (List<Log>) pm.newQuery(query).execute();
		return new LogCol(logs);
	}
}
