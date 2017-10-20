package com.common.powertech.bussiness;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.common.powertech.exception.OtherException;
import com.common.powertech.xml.BillDailyApply_Class;

public class PULLParse_BillDaily_Apply {
	
	private static String rspcod,rspmsg;
	
	public static List<BillDailyApply_Class> getBillDailyApplyList(InputStream is) throws Exception{
		 
		BillDailyApply_Class billDailyApply_Class=new BillDailyApply_Class();
		List<BillDailyApply_Class> list_billDailyApply_class=new ArrayList<BillDailyApply_Class>();
		List<String> list_content=new ArrayList<String>();
		XmlPullParser pullParser=Xml.newPullParser();
		pullParser.setInput(is, "UTF-8");
		int event=pullParser.getEventType();
		
		while(event!=XmlPullParser.END_DOCUMENT){
			switch (event) {
			case XmlPullParser.START_DOCUMENT:				
				break;

			case XmlPullParser.START_TAG:
                if( "RSPCOD".equals(pullParser.getName())){
                    rspcod = pullParser.nextText();
                }
                if( "RSPMSG".equals(pullParser.getName())){
                    rspmsg = pullParser.nextText();
                }
                if(rspcod!=null && rspmsg!=null){
                if(!(rspcod.equals("00000"))){
					String msg = rspmsg;
                	rspcod = null;
                	rspmsg = null;
                	throw new OtherException(msg);
                	}
                }
                if("TOF_DATE".equals(pullParser.getName())){
                	billDailyApply_Class.setTOF_DATE(pullParser.nextText());
                }
                if("TOF_NO".equals(pullParser.getName())){
                	billDailyApply_Class.setTOF_NO(pullParser.nextText());
                }
                if("TOF_OPR".equals(pullParser.getName())){
                	billDailyApply_Class.setTOF_OPR(pullParser.nextText());
                }
                if("TOF_AMT".equals(pullParser.getName())){
                	billDailyApply_Class.setTOF_AMT(pullParser.nextText());
                }
                if("FTA_STATUS".equals(pullParser.getName())){
                	billDailyApply_Class.setFTA_STATUS(pullParser.nextText());
                }
                if("TYPE_T".equals(pullParser.getName())){
                	billDailyApply_Class.setTYPE_T(pullParser.nextText());
                }
                if("TITLE_T".equals(pullParser.getName())){
                	billDailyApply_Class.setTITLE_T(pullParser.nextText());
                }
                if("CONTENT_T".equals(pullParser.getName())){
                	list_content.add(pullParser.nextText());
                }
                if("TAIL_T".equals(pullParser.getName())){
                	billDailyApply_Class.setTAIL_T(pullParser.nextText());
                }
                if("PIC_T".equals(pullParser.getName())){
                	billDailyApply_Class.setPIC_T(pullParser.nextText());
                }
                if("TOKEN_T".equals(pullParser.getName())){
                	billDailyApply_Class.setTOKEN_T(pullParser.nextText());
                }
				break;
				
			case XmlPullParser.END_TAG:
				if("ROOT".equals(pullParser.getName())){
					billDailyApply_Class.setCONTENT_T(list_content);
					list_billDailyApply_class.add(billDailyApply_Class);
					billDailyApply_Class=null;
					rspcod=null;
					rspmsg=null;
				}
				break;
			}
			event=pullParser.next();
		}
		return list_billDailyApply_class;
	}

}
