package com.common.powertech.bussiness;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import com.common.powertech.exception.OtherException;

import android.util.Xml;

public class PULLParse_Logout {
	
	private static String rspcod,rspmsg;
	
	public static void getLogoutList(InputStream is) throws Exception{
		
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
                break;
                
			case XmlPullParser.END_TAG:
				rspcod=null;
				rspmsg=null;
				break;
			}
			event=pullParser.next();
		}
	}

}
