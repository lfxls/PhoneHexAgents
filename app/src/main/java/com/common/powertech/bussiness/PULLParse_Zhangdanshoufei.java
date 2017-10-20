package com.common.powertech.bussiness;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import com.common.powertech.xml.ShoufeiQuery_Class;

import android.util.Log;
import android.util.Xml;

/**
 * 广东天波信息技术股份有限公司 功能：收费查询解析 作者:luyq 日期:2015-10-12
 */
public class PULLParse_Zhangdanshoufei extends PULLParseParent {

	public static List<ShoufeiQuery_Class> getBDList(InputStream is)
			throws Exception {

		ShoufeiQuery_Class bd = null;
		List<ShoufeiQuery_Class> bds = null;
		Class<?> studenClass = null;
		try {
			studenClass = ShoufeiQuery_Class.class;
		} catch (Exception e) {
			e.printStackTrace();
		}

		XmlPullParser pullParser = Xml.newPullParser();
		pullParser.setInput(is, "UTF-8");
		int event = pullParser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {

			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				bds = new ArrayList<ShoufeiQuery_Class>();
				break;

			case XmlPullParser.START_TAG:
				if ("RSPCOD".equals(pullParser.getName())) {
					rspcod = pullParser.nextText();
				}

				if ("RSPMSG".equals(pullParser.getName())) {
					rspmsg = pullParser.nextText();
				}

				if ("TOLCNT".equals(pullParser.getName())) {
					tolcnt = pullParser.nextText();
				}

				if ("STUDENT".equals(pullParser.getName())) {
					bd = (ShoufeiQuery_Class) studenClass.newInstance();
				}

				if (bd != null) {
					Field[] field = studenClass.getDeclaredFields();// 获取全部属性
					for (int i = 0; i < field.length; i++) {
						Log.e("PULLParse_ShoufeiQueryRequest",field[i].getName());
						if (pullParser.getName().equalsIgnoreCase(
								field[i].getName())) {
							Method method = studenClass.getMethod("set"
									+ field[i].getName(), field[i].getType());// 获取set方法
							method.invoke(bd, pullParser.nextText());// 赋值
							break;
						}
					}
				}

				break;

			case XmlPullParser.END_TAG:
				if ("STUDENT".equals(pullParser.getName())) {
					bds.add(bd);
					bd = null;
				}
				break;
			}
			event = pullParser.next();
		}
		return bds;
	}
}
