package com.jaydi.ruby.utils;

import com.google.gson.Gson;

public class JsonUtils {
	private static Gson gson;

	static {
		gson = new Gson();
	}

	public static String toJson(Object obj) {
		return gson.toJson(obj);
	}

	public static <T> T fromJson(String json, Class<T> clazz) {
		return gson.fromJson(json, clazz);
	}

}
