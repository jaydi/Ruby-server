package com.jaydi.ruby.gcm;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import com.jaydi.ruby.models.Event;
import com.jaydi.ruby.models.PMF;
import com.jaydi.ruby.models.User;
import com.jaydi.ruby.utils.JsonUtils;

public class GcmManager {
	private static final Logger log = Logger.getLogger(GcmManager.class.getName());
	private static final String API_KEY = "AIzaSyDwEt90f4QgZmDTys6WioldbQ1A7q9tOow";

	private static final String MESSAGE_KEY = "MESSAGE_KEY";
	private static final String LEVEL_CHANGE_STATE = "LEVEL_CHANGE_STATE";

	private static final String KEY_LEVEL_CHANGE = "KEY_LEVEL_CHANGE";
	private static final String KEY_EVENT_NOTICE = "KEY_EVENT_NOTICE";
	private static final String KEY_EVENT_CLEAR = "KEY_EVENT_CLEAR";

	public static void sendLevelChange(List<Long> userIds, boolean change) {
		String msg = "";
		try {
			Map<String, String> data = new HashMap<String, String>();
			data.put(MESSAGE_KEY, KEY_LEVEL_CHANGE);
			data.put(LEVEL_CHANGE_STATE, String.valueOf(change));

			GcmMessage gcmMsg = new GcmMessage();
			gcmMsg.setRegistration_ids(getRegIds(userIds));
			gcmMsg.setData(data);

			msg = JsonUtils.toJson(gcmMsg);
			URLEncoder.encode(msg, "UTF-8");

		} catch (UnsupportedEncodingException e) {
		}

		sendGcm(msg);
	}

	public static void sendEventNotice(List<Long> userIds, Event event) {
		String msg = "";
		try {
			Map<String, String> data = new HashMap<String, String>();
			data.put(MESSAGE_KEY, KEY_EVENT_NOTICE);
			data.put("id", "" + event.getId());
			data.put("userId", "" + event.getUserId());
			data.put("targetId", "" + event.getTargetId());
			data.put("message", URLEncoder.encode(event.getMessage(), "UTF-8"));
			data.put("type", "" + event.getType());
			data.put("ruby", "" + event.getRuby());

			GcmMessage gcmMsg = new GcmMessage();
			gcmMsg.setRegistration_ids(getRegIds(userIds));
			gcmMsg.setData(data);

			msg = JsonUtils.toJson(gcmMsg);

		} catch (UnsupportedEncodingException e) {
		}

		sendGcm(msg);
	}

	public static void sendEventClear(List<Long> userIds, Event event) {
		String msg = "";
		try {
			Map<String, String> data = new HashMap<String, String>();
			data.put(MESSAGE_KEY, KEY_EVENT_CLEAR);
			data.put("id", "" + event.getId());
			data.put("userId", "" + event.getUserId());
			data.put("targetId", "" + event.getTargetId());
			data.put("message", URLEncoder.encode(event.getMessage(), "UTF-8"));
			data.put("type", "" + event.getType());
			data.put("ruby", "" + event.getRuby());

			GcmMessage gcmMsg = new GcmMessage();
			gcmMsg.setRegistration_ids(getRegIds(userIds));
			gcmMsg.setData(data);

			msg = JsonUtils.toJson(gcmMsg);
			URLEncoder.encode(msg, "UTF-8");

		} catch (UnsupportedEncodingException e) {
		}

		sendGcm(msg);
	}

	private static List<String> getRegIds(List<Long> userIds) {
		List<User> users = new ArrayList<User>();
		PersistenceManager pm = PMF.getPersistenceManager();
		for (Long userId : userIds)
			try {
				User user = pm.getObjectById(User.class, userId);
				users.add(user);

			} catch (JDOObjectNotFoundException e) {
			}

		List<String> regIds = new ArrayList<String>();
		for (User user : users)
			regIds.add(user.getRegId());

		return regIds;
	}

	private static void sendGcm(String msg) {
		log.info("gcm message: " + msg);
		try {
			URL url = new URL("https://android.googleapis.com/gcm/send");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.addRequestProperty("Content-Type", "application/json");
			connection.addRequestProperty("Authorization", "key=" + API_KEY);

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write(msg);
			writer.close();

			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
				log.info("send gcm success");
			else
				log.info("send gcm failed: " + connection.getResponseCode());

		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}
	}
}
