package com.common.powertech.bussiness;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.common.powertech.exception.OtherException;
import com.common.powertech.xml.UpdateAndVerifyTime_Class;

public class PULLParse_UpdateAndVerifyTime {
	
	private static String rspcod,rspmsg;
	
	public static List<UpdateAndVerifyTime_Class> getUpdateAndVerifyTimeList(InputStream is) throws Exception{
		
		UpdateAndVerifyTime_Class updateAndVerifyTime_Class=new UpdateAndVerifyTime_Class();
		List<UpdateAndVerifyTime_Class> list_updateAndVerifyTime_class=new ArrayList<UpdateAndVerifyTime_Class>();
		
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
                if("SERVER_DATETIME".equals(pullParser.getName())){
                	updateAndVerifyTime_Class.setSERVER_DATETIME(pullParser.nextText());
                }
                if("U_VERSION".equals(pullParser.getName())){
                	updateAndVerifyTime_Class.setU_VERSION(pullParser.nextText());
                }
                if("U_RULE".equals(pullParser.getName())){
                	updateAndVerifyTime_Class.setU_RULE(pullParser.nextText());
                }
                if("U_URL".equals(pullParser.getName())){
                	updateAndVerifyTime_Class.setU_URL(pullParser.nextText());
                }
                if("TEMP_VERSION".equals(pullParser.getName())){
                	updateAndVerifyTime_Class.setTEMP_VERSION(pullParser.nextText());
                }
                break;
                
			case XmlPullParser.END_TAG:
				if("ROOT".equals(pullParser.getName())){
					list_updateAndVerifyTime_class.add(updateAndVerifyTime_Class);
					updateAndVerifyTime_Class=null;
					rspcod=null;
					rspmsg=null;
				}
				break;
			}
			event=pullParser.next();
		}
		return list_updateAndVerifyTime_class;
	}

}
