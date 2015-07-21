package com.jaydi.ruby.utils;

import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtils {

	public static long getNowTime() {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
		return c.getTimeInMillis();
	}

	public static long getCouponExpirationTime() {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
		c.add(Calendar.DAY_OF_MONTH, 60);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		return c.getTimeInMillis();
	}

	public static Object getDawnTime() {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 100);
		return c.getTimeInMillis();
	}
}
