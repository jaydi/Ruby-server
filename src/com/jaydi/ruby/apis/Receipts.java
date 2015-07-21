package com.jaydi.ruby.apis;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.jaydi.ruby.constants.Constants;
import com.jaydi.ruby.exceptions.ReadReceiptException;
import com.jaydi.ruby.models.BaseModel;
import com.jaydi.ruby.models.PMF;
import com.jaydi.ruby.models.Receipt;
import com.jaydi.ruby.models.Ruby;
import com.jaydi.ruby.models.User;

@Api(name = "rubymine", version = "v1", clientIds = { Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID, Constants.WEB_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID })
public class Receipts {

	@ApiMethod(name = "receipts.claim", path = "receipt/claim", httpMethod = "post")
	public User claim(Receipt receipt) {
		PersistenceManager pm = PMF.getPersistenceManager();
		try {
			User user = pm.getObjectById(User.class, receipt.getUserId());
			Receipt target = pm.getObjectById(Receipt.class, receipt.getId());

			if (target.getUserId() != 0)
				throw new ReadReceiptException();
			target.setUserId(receipt.getUserId());

			int rubyValue = 1 + (target.getPrice() / 4000);
			Ruby ruby = new Ruby();
			ruby.setUserId(target.getUserId());
			ruby.setPlanterId(target.getShopId());
			ruby.setValue(rubyValue);
			ruby.setEvent(Ruby.EVENT_PURCHASE);
			ruby.setCreatedAt(target.getCreatedAt());
			pm.makePersistent(ruby);

			user.setRuby(user.getRuby() + ruby.getValue());

			pm.close();

			return user;

		} catch (JDOObjectNotFoundException e) {
			User user = new User();
			user.setId(0l);
			user.setResultCode(BaseModel.NO_DATA);
			return user;
		} catch (ReadReceiptException e) {
			User user = new User();
			user.setId(0l);
			user.setResultCode(BaseModel.READ_RECEIPT);
			return user;
		}
	}
}
