package com.common.powertech.bussiness;

import java.util.TreeMap;

import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Md5Algorithm;
import com.common.powertech.util.SystemUtil;

/**
 * Created by yeqw on 2015/10/23.
 */
public class Request_Peach_Pay extends ParentRequset{
	private static String amount,unit,regist_flag,pay_brand,cardNo,cardHolder,expiryM,expiryY,card_cvv,regist_id;

	public static void setRegist_id(String regist_id) {
		Request_Peach_Pay.regist_id = regist_id;
	}


	public static void setAmount(String amount) {
		Request_Peach_Pay.amount = amount;
	}

	public static void setUnit(String unit) {
		Request_Peach_Pay.unit = unit;
	}

	public static void setRegist_flag(String regist_flag) {
		Request_Peach_Pay.regist_flag = regist_flag;
	}

	public static void setPay_brand(String pay_brand) {
		Request_Peach_Pay.pay_brand = pay_brand;
	}

	public static void setCardNo(String cardNo) {
		Request_Peach_Pay.cardNo = cardNo;
	}

	public static void setCardHolder(String cardHolder) {
		Request_Peach_Pay.cardHolder = cardHolder;
	}

	public static void setExpiryM(String expiryM) {
		Request_Peach_Pay.expiryM = expiryM;
	}

	public static void setExpiryY(String expiryY) {
		Request_Peach_Pay.expiryY = expiryY;
	}

	public static void setCard_cvv(String card_cvv) {
		Request_Peach_Pay.card_cvv = card_cvv;
	}


	/****
	 * String PageNum --- 当前页码 默认1
	 * 
	 * String NumperPag --- 每页数据条 默认10
	 * 
	 * String FromDate --- 开始时间 时间格式：2014-12-27
	 * 
	 * String ToDate --- 结束时间 时间格式：2014-12-27
	 * 
	 * ***/
	public static String getBODY() {
		String body = "";
		body = "<BODY>";
		
//		amount,unit,regist_flag,pay_brand,cardNo,cardHolder,expiryM,expiryY,card_cvv;
		
		if(amount.length()>0){
            body += "<AMOUNT>"+amount+"</AMOUNT>";
        }
		if(unit.length()>0){
            body += "<UNIT>"+unit+"</UNIT>";
        }
		if(regist_flag.length()>0){
            body += "<REGIST_FLAG>"+regist_flag+"</REGIST_FLAG>";
        }
		if(pay_brand.length()>0){
            body += "<PAY_BRAND>"+pay_brand+"</PAY_BRAND>";
        }
		if(cardNo.length()>0){
            body += "<CARDNO>"+cardNo+"</CARDNO>";
        }
		if(cardHolder.length()>0){
            body += "<CARDHOLDER>"+cardHolder+"</CARDHOLDER>";
        }
		if(expiryM.length()>0){
            body += "<EXPIRYM>"+expiryM+"</EXPIRYM>";
        }
		if(expiryY.length()>0){
            body += "<EXPIRYY>"+expiryY+"</EXPIRYY>";
        }
		if(card_cvv.length()>0){
            body += "<CARD_CVV>"+card_cvv+"</CARD_CVV>";
        }

		if(regist_id.length()>0){
            body += "<REGIST_ID>"+regist_id+"</REGIST_ID>";
        }
		
		

		body += "</BODY>";

		return body;
	}

	public static String getMD5str() {

		TreeMap<String, String> paramMap = new TreeMap<String, String>();
		// TOP
		paramMap.put("IMEI", SystemUtil.getIMEI(context));
		paramMap.put("SESSION_ID", GlobalParams.SESSION_ID);
		paramMap.put("REQUEST_TIME", date);
		paramMap.put("LOCAL_LANGUAGE", SystemUtil.getLocalLanguage(getContext()));

		// BODY
//		amount,unit,regist_flag,pay_brand,cardNo,cardHolder,expiryM,expiryY,card_cvv;
		paramMap.put("AMOUNT", amount);
		paramMap.put("UNIT", unit);
		paramMap.put("REGIST_FLAG", regist_flag);
		paramMap.put("PAY_BRAND", pay_brand);
		paramMap.put("CARDNO", cardNo);
		paramMap.put("CARDHOLDER", cardHolder);
		paramMap.put("EXPIRYM", expiryM);
//		paramMap.put("EXPIRYY", expiryY);
//		paramMap.put("CARD_CVV", card_cvv);
//		paramMap.put("REGIST_ID", regist_id);


		// TAIL
		paramMap.put("SIGN_TYPE", "1");

		String signStr = bulidParam(paramMap);
		String sign = Md5Algorithm.getInstance().md5Digest(signStr.getBytes());
		return sign;
	}

    public static String getRequsetXML() {
        return "<ROOT>" + getTOP() + getBODY() + getTAIL(getMD5str())
                + "</ROOT>";
    }




}
