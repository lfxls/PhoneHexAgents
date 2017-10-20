package com.common.powertech.bussiness;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class PULLParse_PeachList_Query {
	private static String rspcod, rspmsg, tolcnt;
	private static List<PeachList_Class> pls;
	private static List<PeachSavedList_Class> psls;
	
	public static void getPeach(InputStream is) throws Exception{
		PeachList_Class pl = null;
        pls = null;
        
        PeachSavedList_Class psl = null;
		psls = null;
        
        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(is,"UTF-8");
        int event = pullParser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT){

            switch (event){
                case XmlPullParser.START_DOCUMENT:
                    pls = new ArrayList<PeachList_Class>();
                    psls = new ArrayList<PeachSavedList_Class>();
                    break;
                    
                case XmlPullParser.START_TAG:
                	if( "RSPCOD".equals( pullParser.getName() ) ){
                        rspcod = pullParser.nextText();
                    }

                    if( "RSPMSG".equals( pullParser.getName() ) ){
                        rspmsg = pullParser.nextText();
                    }

                    if( "TOLCNT".equals( pullParser.getName() ) ){
                        tolcnt = pullParser.nextText();
                    }
                    
                    if( "STUDENT".equals( pullParser.getName() ) ){
                        pl = new PeachList_Class();
                    }
                    
                 
                    if(pl!=null){
                    
                    	if( "Enm_Dat_Opt".equals( pullParser.getName() ) ){
                    		pl.setEnm_Dat_Opt(pullParser.nextText());
                    	}
                    	
                    	if( "Enm_Dat_Des".equals( pullParser.getName() ) ){
                    		pl.setEnm_Dat_Des(pullParser.nextText());
                    	}
                    }
                    
                    
                    if( "STUDENT2".equals( pullParser.getName() ) ){
                        psl = new PeachSavedList_Class();
                    }
                   
                    
                    if(psl!=null){
                        
                    	if( "CARD_NO".equals( pullParser.getName() ) ){
                    		psl.setCARD_NO(pullParser.nextText());
                    	}
                    	
                    	if( "EXPIRY_DATE".equals( pullParser.getName() ) ){
                    		psl.setEXPIRY_DATE(pullParser.nextText());
                    	}
                    	
                    	if( "HOLDER_NAME".equals( pullParser.getName() ) ){
                    		psl.setHOLDER_NAME(pullParser.nextText());
                    	}
                    	
                    	if( "PEACH_TYPE".equals( pullParser.getName() ) ){
                    		psl.setPEACH_TYPE(pullParser.nextText());
                    	}
                    	
                    	if( "REGIST_ID".equals( pullParser.getName() ) ){
                    		psl.setREGIST_ID(pullParser.nextText());
                    	}
                    	
                    	if( "TYPE_NAME".equals( pullParser.getName() ) ){
                    		psl.setTYPE_NAME(pullParser.nextText());
                    	}
                    }
          
                    break;
                    
                case XmlPullParser.END_TAG:
                    if("STUDENT".equals( pullParser.getName() ) ){
                        pls.add(pl);
                        pl = null;
                        
                    }
                    
                    if("STUDENT2".equals( pullParser.getName() ) ){

                        psls.add(psl);
                        psl = null;
                    }
                  
                    break;
                    
                    

            }
            event = pullParser.next();
        }
        
	}
	
	public static List<PeachList_Class> getPeachList(){
		return pls;
	} 
	
	public static List<PeachSavedList_Class> getPeachSavedList(){
		return psls;
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
