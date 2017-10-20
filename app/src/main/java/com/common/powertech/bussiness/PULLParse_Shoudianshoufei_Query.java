package com.common.powertech.bussiness;

import java.io.InputStream;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

/**
 * Created by ouyangguozhao on 2015/10/15.
 */
public class PULLParse_Shoudianshoufei_Query {

	private static String rspcod, rspmsg;

	public static HashMap<String, String> getBillPayMap(InputStream is)
			throws Exception {
		HashMap<String, String> map = null;
		XmlPullParser pullParser = Xml.newPullParser();
		pullParser.setInput(is, "UTF-8");
		int event = pullParser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {

			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				map = new HashMap<String, String>();
				break;

			case XmlPullParser.START_TAG:
				if ("RSPCOD".equals(pullParser.getName())) {
					map.put("RSPCOD", pullParser.nextText());
				}
				if ("RSPMSG".equals(pullParser.getName())) {
					map.put("RSPMSG", pullParser.nextText());
				}
				if ("PRDORDNO".equals(pullParser.getName())) {
					map.put("PRDORDNO", pullParser.nextText());
				}
				if ("ORDER_TIME".equals(pullParser.getName())) {
					map.put("ORDER_TIME", pullParser.nextText());
				}
				if ("AMT".equals(pullParser.getName())) {
					map.put("AMT", pullParser.nextText());
				}
				if ("PAY_AMT".equals(pullParser.getName())) {
					map.put("PAY_AMT", pullParser.nextText());
				}
				if ("FEE_AMT".equals(pullParser.getName())) {
					map.put("FEE_AMT", pullParser.nextText());
				}
				if ("ENERGY_NUM".equals(pullParser.getName())) {
					map.put("ENERGY_NUM", pullParser.nextText());
				}
				if ("TOKEN".equals(pullParser.getName())) {
					map.put("TOKEN", pullParser.nextText());
				}
				if ("BANLANCE".equals(pullParser.getName())) {
					map.put("BANLANCE", pullParser.nextText());
				}
				if ("IC_JSON_RES".equals(pullParser.getName())) {
					map.put("IC_JSON_RES", pullParser.nextText());
				}
				break;

			case XmlPullParser.END_TAG:
				break;
			}
			event = pullParser.next();
		}
		return map;
	}

	public static String getRspcod() {
		return rspcod;
	}

	public static String getRspmsg() {
		return rspmsg;
	}
}
