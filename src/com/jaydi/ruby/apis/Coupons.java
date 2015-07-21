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
import com.jaydi.ruby.exceptions.NoGemLeftException;
import com.jaydi.ruby.models.BaseModel;
import com.jaydi.ruby.models.Coupon;
import com.jaydi.ruby.models.CouponCol;
import com.jaydi.ruby.models.Gem;
import com.jaydi.ruby.models.Job;
import com.jaydi.ruby.models.PMF;
import com.jaydi.ruby.models.User;
import com.jaydi.ruby.utils.TimeUtils;

@Api(name = "rubymine", version = "v1", clientIds = { Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID, Constants.WEB_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID })
public class Coupons {

	@ApiMethod(name = "coupons.insert", path = "coupon", httpMethod = "post")
	public Coupon insert(Coupon coupon) {
		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(coupon);
		pm.close();
		return coupon;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "coupons.list", path = "coupons", httpMethod = "get")
	public CouponCol list(@Named("userId") long userId) {
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(Coupon.class);
		List<Coupon> cans = (List<Coupon>) pm.newQuery(query).execute();

		List<Coupon> coupons = new ArrayList<Coupon>();
		for (Coupon can : cans)
			if (can.getUserId().equals(userId))
				coupons.add(can);

		CouponCol couponCol = new CouponCol();
		couponCol.setCoupons(coupons);

		return couponCol;
	}

	@ApiMethod(name = "coupons.delete", path = "coupon", httpMethod = "delete")
	public void delete(@Named("id") long id) {
		PersistenceManager pm = PMF.getPersistenceManager();
		try {
			Coupon target = pm.getObjectById(Coupon.class, id);
			pm.deletePersistent(target);

		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
	}

	@ApiMethod(name = "coupons.redeem", path = "coupon/redeem", httpMethod = "post")
	public User redeem(Coupon coupon) {
		PersistenceManager pm = PMF.getPersistenceManager();
		User user = null;

		try {
			user = pm.getObjectById(User.class, coupon.getUserId());
			Gem gem = pm.getObjectById(Gem.class, coupon.getGemId());

			if (gem.getEa() < 1)
				throw new NoGemLeftException();

			user.setRuby(user.getRuby() - gem.getValue());
			gem.setEa(gem.getEa() - 1);

			coupon.setExpirationDate(TimeUtils.getCouponExpirationTime());
			pm.makePersistent(coupon);

			Job expJob = new Job();
			expJob.setType(Job.TYPE_EXPIRE_COUPON);
			expJob.setTargetId(coupon.getId());
			expJob.setScheduledTime(coupon.getExpirationDate());
			pm.makePersistent(expJob);

		} catch (JDOObjectNotFoundException e) {
			user = new User();
			user.setId(0l);
			user.setResultCode(BaseModel.NO_DATA);
			return user;
		} catch (NoGemLeftException e) {
			user = new User();
			user.setId(0l);
			user.setResultCode(BaseModel.NO_GEM);
			return user;
		} finally {
			pm.close();
		}

		return user;
	}

	@ApiMethod(name = "coupons.use", path = "coupon/use", httpMethod = "post")
	public Coupon use(Coupon coupon) {
		PersistenceManager pm = PMF.getPersistenceManager();
		Coupon target = null;

		try {
			target = pm.getObjectById(Coupon.class, coupon.getId());
			target.setState(Coupon.STATE_USED);

		} catch (JDOObjectNotFoundException e) {
			target = new Coupon();
			target.setResultCode(BaseModel.NO_DATA);
			return target;
		}

		return target;
	}

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "coupons.all", path = "coupon/all", httpMethod = "get")
	public CouponCol all() {
		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(Coupon.class);
		List<Coupon> coupons = (List<Coupon>) pm.newQuery(query).execute();
		return new CouponCol(coupons);
	}

}
