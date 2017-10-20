package com.common.powertech.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.powertech.param.GlobalParams;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

	/**
	 * An array of sample (dummy) items.
	 */
	public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

   public static void setItem(){
		if (GlobalParams.LANGUAGE.equals("zh")) {
			if (null != GlobalParams.MENU_NAME
					&& GlobalParams.MENU_NAME.size() > 0) {
				for (int i = 1; i < GlobalParams.MENU_NAME.size(); i++) {
					if(GlobalParams.MENU_NAME.get(i).equals("收费")){
						if("TPS390".equalsIgnoreCase(GlobalParams.DeviceModel)){
							addItem(new DummyItem("1", GlobalParams.MENU_NAME.get(i)+""));
						}else{
							addItem(new DummyItem("1", GlobalParams.MENU_NAME.get(i)+"(##)"));
						}
					}else if (GlobalParams.MENU_NAME.get(i).equals("凭证补打")) {
						addItem(new DummyItem("2", GlobalParams.MENU_NAME.get(i)+""));
					}else if (GlobalParams.MENU_NAME.get(i).equals("冲正申请")) {
						addItem(new DummyItem("3", GlobalParams.MENU_NAME.get(i)+""));
					}else if (GlobalParams.MENU_NAME.get(i).equals("收费日结")) {
						addItem(new DummyItem("4", GlobalParams.MENU_NAME.get(i)+""));
					}else if (GlobalParams.MENU_NAME.get(i).equals("日结确认")) {
						addItem(new DummyItem("5", GlobalParams.MENU_NAME.get(i)+""));
					}else if (GlobalParams.MENU_NAME.get(i).equals("押金充值")) {
						addItem(new DummyItem("8", GlobalParams.MENU_NAME.get(i)+""));	
					}else if (GlobalParams.MENU_NAME.get(i).equals("收支明细")) {
						addItem(new DummyItem("6", GlobalParams.MENU_NAME.get(i)+""));
					}else if (GlobalParams.MENU_NAME.get(i).equals("销售订单")) {
						addItem(new DummyItem("9", GlobalParams.MENU_NAME.get(i)+""));
					}else if (GlobalParams.MENU_NAME.get(i).equals("转账记录")) {
						addItem(new DummyItem("10", GlobalParams.MENU_NAME.get(i)+""));
					}
				}
			} else {
				addItem(new DummyItem("1", ""));
			}
			if(GlobalParams.DeviceModel.equals("TPS390")){
				addItem(new DummyItem("7", "系统设置"));
			}
		} else if (GlobalParams.LANGUAGE.equals("en")) {
			if (null != GlobalParams.MENU_NAME_EN
					&& GlobalParams.MENU_NAME_EN.size() > 0
					&& null != GlobalParams.MENU_NAME
					&& GlobalParams.MENU_NAME.size() > 0) {
				for (int i = 1; i < GlobalParams.MENU_NAME_EN.size(); i++) {
					if(GlobalParams.MENU_NAME.get(i).equals("收费")){
						if("TPS390".equalsIgnoreCase(GlobalParams.DeviceModel)){
							addItem(new DummyItem("1", GlobalParams.MENU_NAME_EN.get(i)+""));
						}else{
							addItem(new DummyItem("1", GlobalParams.MENU_NAME_EN.get(i)+"(##)"));
						}
						
					}else if (GlobalParams.MENU_NAME.get(i).equals("凭证补打")) {
						addItem(new DummyItem("2", GlobalParams.MENU_NAME_EN.get(i)+""));
					}else if (GlobalParams.MENU_NAME.get(i).equals("冲正申请")) {
						addItem(new DummyItem("3", GlobalParams.MENU_NAME_EN.get(i)+""));
					}else if (GlobalParams.MENU_NAME.get(i).equals("收费日结")) {
						addItem(new DummyItem("4", GlobalParams.MENU_NAME_EN.get(i)+""));
					}else if (GlobalParams.MENU_NAME.get(i).equals("日结确认")) {
						addItem(new DummyItem("5", GlobalParams.MENU_NAME_EN.get(i)+""));
					}else if (GlobalParams.MENU_NAME.get(i).equals("收支明细")) {
						addItem(new DummyItem("6", GlobalParams.MENU_NAME_EN.get(i)+""));
					}else if (GlobalParams.MENU_NAME.get(i).equals("押金充值")) {
						addItem(new DummyItem("8", GlobalParams.MENU_NAME_EN.get(i)+""));
					}else if (GlobalParams.MENU_NAME.get(i).equals("销售订单")) {
						addItem(new DummyItem("9", GlobalParams.MENU_NAME_EN.get(i)+""));
					}else if (GlobalParams.MENU_NAME.get(i).equals("转账记录")) {
						addItem(new DummyItem("10", GlobalParams.MENU_NAME_EN.get(i)+""));
					}
				}
			} else {
				addItem(new DummyItem("1", ""));
			}
			if(GlobalParams.DeviceModel.equals("TPS390")){
				addItem(new DummyItem("7", "Setting"));
			}
		} else if (GlobalParams.LANGUAGE.equals("fr")) {
			if (null != GlobalParams.MENU_NAME_FR
					&& GlobalParams.MENU_NAME_FR.size() > 0
					&& null != GlobalParams.MENU_NAME_EN
					&& GlobalParams.MENU_NAME_EN.size() > 0
					&& null != GlobalParams.MENU_NAME
					&& GlobalParams.MENU_NAME.size() > 0) {
				for (int i = 1; i < GlobalParams.MENU_NAME_FR.size(); i++) {
					if(GlobalParams.MENU_NAME.get(i).equals("收费")){
						if("TPS390".equalsIgnoreCase(GlobalParams.DeviceModel)){
							addItem(new DummyItem("1", GlobalParams.MENU_NAME_FR.get(i)+""));
						}else{
							addItem(new DummyItem("1", GlobalParams.MENU_NAME_FR.get(i)+"(##)"));
						}
					}else if (GlobalParams.MENU_NAME.get(i).equals("凭证补打")) {
						addItem(new DummyItem("2", GlobalParams.MENU_NAME_FR.get(i)+""));
					}else if (GlobalParams.MENU_NAME.get(i).equals("冲正申请")) {
						addItem(new DummyItem("3", GlobalParams.MENU_NAME_FR.get(i)+""));
					}else if (GlobalParams.MENU_NAME.get(i).equals("收费日结")) {
						addItem(new DummyItem("4", GlobalParams.MENU_NAME_FR.get(i)+""));
					}else if (GlobalParams.MENU_NAME.get(i).equals("日结确认")) {
						addItem(new DummyItem("5", GlobalParams.MENU_NAME_FR.get(i)+""));
					}else if (GlobalParams.MENU_NAME.get(i).equals("收支明细")) {
						addItem(new DummyItem("6", GlobalParams.MENU_NAME_FR.get(i)+""));
					}else if (GlobalParams.MENU_NAME.get(i).equals("押金充值")) {
						addItem(new DummyItem("8", GlobalParams.MENU_NAME_FR.get(i)+""));
					}else if (GlobalParams.MENU_NAME.get(i).equals("销售订单")) {
						addItem(new DummyItem("9", GlobalParams.MENU_NAME_FR.get(i)+""));
					}else if (GlobalParams.MENU_NAME.get(i).equals("转账记录")) {
						addItem(new DummyItem("10", GlobalParams.MENU_NAME_FR.get(i)+""));
					}
				}
			} else {
				addItem(new DummyItem("1", ""));
			}
			if(GlobalParams.DeviceModel.equals("TPS390")){
				addItem(new DummyItem("7", "Configuration"));
			}
		}else {
			addItem(new DummyItem("1", ""));
		}
	}

	private static void addItem(DummyItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	public static class DummyItem {
		public String id;
		public String content;

		public DummyItem(String id, String content) {
			this.id = id;
			this.content = content;
		}

		@Override
		public String toString() {
			return content;
		}
	}
}
