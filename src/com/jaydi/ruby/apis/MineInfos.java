package com.jaydi.ruby.apis;

import java.util.List;

import javax.inject.Named;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.jaydi.ruby.constants.Constants;
import com.jaydi.ruby.models.BaseModel;
import com.jaydi.ruby.models.MineInfo;
import com.jaydi.ruby.models.MineInfoCol;
import com.jaydi.ruby.models.PMF;

@Api(name = "rubymine", version = "v1", clientIds = { Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID, Constants.WEB_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID })
public class MineInfos {

	@ApiMethod(name = "mineinfos.insert", path = "mineinfo", httpMethod = "post")
	public MineInfo insert(MineInfo info) {
		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(info);
		pm.close();
		return info;
	}

	@ApiMethod(name = "mineinfos.get", path = "mineinfo", httpMethod = "get")
	public MineInfo get(@Named("id") long id) {
		PersistenceManager pm = PMF.getPersistenceManager();
		MineInfo info;
		try {
			info = pm.getObjectById(MineInfo.class, id);

		} catch (JDOObjectNotFoundException e) {
			info = new MineInfo();
			info.setResultCode(BaseModel.NO_DATA);
		}

		return info;
	}

	@ApiMethod(name = "mineinfos.update", path = "mineinfo", httpMethod = "put")
	public MineInfo update(MineInfo info) {
		PersistenceManager pm = PMF.getPersistenceManager();
		MineInfo target;
		try {
			target = pm.getObjectById(MineInfo.class, info.getId());
			target.update(info);

		} catch (JDOObjectNotFoundException e) {
			target = new MineInfo();
			target.setResultCode(BaseModel.NO_DATA);
		}

		return target;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "mineinfos.all", path = "mineInfo/all", httpMethod = "get")
	public MineInfoCol all() {
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(MineInfo.class);
		List<MineInfo> mineInfos = (List<MineInfo>) pm.newQuery(query).execute();
		return new MineInfoCol(mineInfos);
	}

}
