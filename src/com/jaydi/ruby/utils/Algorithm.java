package com.jaydi.ruby.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.jaydi.ruby.models.Ruby;
import com.jaydi.ruby.models.Rubymine;

public class Algorithm {
	// private static final Logger log = Logger.getLogger(Algorithm.class.getName());

	@SuppressWarnings("unchecked")
	public static List<Rubymine> getRecommendedRubymines(PersistenceManager pm, int rubyEvent, Rubymine planter) {
		int[] recTypes = getRecTypes(rubyEvent, planter.getType());

		Query query = pm.newQuery(Rubymine.class);
		List<Rubymine> rubymines = (List<Rubymine>) pm.newQuery(query).execute();

		List<Rubymine> recs = new ArrayList<Rubymine>();
		for (int i = 0; i < recTypes.length; i++)
			if (recTypes[i] != -1) {
				List<Rubymine> cans = new ArrayList<Rubymine>();
				for (Rubymine rubymine : rubymines)
					if (!rubymine.getId().equals(planter.getId()) && rubymine.getType() == recTypes[i])
						cans.add(rubymine);

				if (!cans.isEmpty())
					recs.add(getRandom(cans));
			}

		return recs;
	}

	public static int[] getRecTypes(int rubyEvent, int planterType) {
		int hour = getHour();
		int[] recTypes = new int[] { -1, -1, -1 };

		if (rubyEvent == Ruby.EVENT_ZONE_IN) {
			if (hour >= 6 && hour < 11) {
				recTypes[0] = Rubymine.TYPE_CAFE;
				recTypes[1] = Rubymine.TYPE_ETC;
			} else if (hour >= 11 && hour < 13) {
				recTypes[0] = Rubymine.TYPE_RESTAURANT;
				recTypes[1] = Rubymine.TYPE_CAFE;
				recTypes[2] = Rubymine.TYPE_ETC;
			} else if (hour >= 13 && hour < 17) {
				recTypes[0] = Rubymine.TYPE_CAFE;
				recTypes[1] = Rubymine.TYPE_ETC;
			} else if (hour >= 17 && hour < 19) {
				recTypes[0] = Rubymine.TYPE_RESTAURANT;
				recTypes[1] = Rubymine.TYPE_CAFE;
				recTypes[2] = Rubymine.TYPE_ETC;
			} else {
				recTypes[0] = Rubymine.TYPE_CAFE;
				recTypes[1] = Rubymine.TYPE_BAR;
				recTypes[2] = Rubymine.TYPE_ETC;
			}
		} else {
			if (planterType == Rubymine.TYPE_RESTAURANT) {
				if (hour >= 6 && hour < 18) {
					recTypes[0] = Rubymine.TYPE_CAFE;
					recTypes[1] = Rubymine.TYPE_ETC;
				} else {
					recTypes[0] = Rubymine.TYPE_BAR;
					recTypes[1] = Rubymine.TYPE_CAFE;
					recTypes[2] = Rubymine.TYPE_ETC;
				}
			} else if (planterType == Rubymine.TYPE_CAFE) {
				if (hour >= 6 && hour < 11) {
					recTypes[0] = Rubymine.TYPE_ETC;
				} else if (hour >= 11 && hour < 13) {
					recTypes[0] = Rubymine.TYPE_RESTAURANT;
					recTypes[1] = Rubymine.TYPE_ETC;
				} else if (hour >= 13 && hour < 17) {
				} else if (hour >= 17 && hour < 19) {
					recTypes[0] = Rubymine.TYPE_RESTAURANT;
					recTypes[1] = Rubymine.TYPE_ETC;
				} else {
					recTypes[0] = Rubymine.TYPE_BAR;
					recTypes[1] = Rubymine.TYPE_ETC;
				}
			} else if (planterType == Rubymine.TYPE_BAR) {
				if (hour >= 6 && hour < 11) {
					recTypes[0] = Rubymine.TYPE_CAFE;
					recTypes[1] = Rubymine.TYPE_ETC;
				} else if (hour >= 11 && hour < 13) {
					recTypes[0] = Rubymine.TYPE_RESTAURANT;
					recTypes[1] = Rubymine.TYPE_ETC;
				} else if (hour >= 13 && hour < 17) {
					recTypes[0] = Rubymine.TYPE_CAFE;
					recTypes[1] = Rubymine.TYPE_ETC;
				} else if (hour >= 17 && hour < 19) {
					recTypes[0] = Rubymine.TYPE_RESTAURANT;
					recTypes[1] = Rubymine.TYPE_CAFE;
					recTypes[2] = Rubymine.TYPE_ETC;
				} else {
					recTypes[0] = Rubymine.TYPE_ETC;
					recTypes[1] = Rubymine.TYPE_BAR;
				}
			} else if (planterType == Rubymine.TYPE_ETC) {
				if (hour >= 6 && hour < 11) {
					recTypes[0] = Rubymine.TYPE_CAFE;
					recTypes[2] = Rubymine.TYPE_ETC;
				} else if (hour >= 11 && hour < 13) {
					recTypes[0] = Rubymine.TYPE_RESTAURANT;
					recTypes[2] = Rubymine.TYPE_ETC;
				} else if (hour >= 13 && hour < 17) {
					recTypes[0] = Rubymine.TYPE_CAFE;
					recTypes[2] = Rubymine.TYPE_ETC;
				} else if (hour >= 17 && hour < 19) {
					recTypes[0] = Rubymine.TYPE_RESTAURANT;
					recTypes[1] = Rubymine.TYPE_CAFE;
					recTypes[2] = Rubymine.TYPE_ETC;
				} else {
					recTypes[0] = Rubymine.TYPE_BAR;
					recTypes[1] = Rubymine.TYPE_CAFE;
					recTypes[2] = Rubymine.TYPE_ETC;
				}
			} else {
				if (hour >= 6 && hour < 11) {
					recTypes[0] = Rubymine.TYPE_CAFE;
					recTypes[1] = Rubymine.TYPE_ETC;
				} else if (hour >= 11 && hour < 13) {
					recTypes[0] = Rubymine.TYPE_RESTAURANT;
					recTypes[1] = Rubymine.TYPE_CAFE;
					recTypes[2] = Rubymine.TYPE_ETC;
				} else if (hour >= 13 && hour < 17) {
					recTypes[0] = Rubymine.TYPE_CAFE;
					recTypes[1] = Rubymine.TYPE_ETC;
				} else if (hour >= 17 && hour < 19) {
					recTypes[0] = Rubymine.TYPE_RESTAURANT;
					recTypes[1] = Rubymine.TYPE_CAFE;
					recTypes[2] = Rubymine.TYPE_ETC;
				} else {
					recTypes[0] = Rubymine.TYPE_CAFE;
					recTypes[1] = Rubymine.TYPE_BAR;
					recTypes[2] = Rubymine.TYPE_ETC;
				}
			}
		}

		return recTypes;
	}

	private static int getHour() {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
		return c.get(Calendar.HOUR_OF_DAY);
	}

	private static Rubymine getRandom(List<Rubymine> cans) {
		Collections.shuffle(cans);
		return cans.get(0);
	}

}
