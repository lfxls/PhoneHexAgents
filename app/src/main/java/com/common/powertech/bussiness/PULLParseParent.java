package com.common.powertech.bussiness;

/**
 * 广东天波信息技术股份有限公司 功能：XML解析器父类 作者:luyq 日期:2015-10-12
 */
public abstract class PULLParseParent {
	protected static String rspcod, rspmsg, tolcnt;

	public static void setRspcod(String rspcod) {
		PULLParseParent.rspcod = rspcod;
	}

	public static void setRspmsg(String rspmsg) {
		PULLParseParent.rspmsg = rspmsg;
	}

	public static void setTolcnt(String tolcnt) {
		PULLParseParent.tolcnt = tolcnt;
	}

	public static String getRspcod() {
		return rspcod;
	}

	public static String getRspmsg() {
		return rspmsg;
	}

	public static String getTolcnt() {
		return tolcnt;
	}
}
