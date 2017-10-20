package com.common.powertech.bussiness;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import android.util.Xml;

import com.common.powertech.exception.OtherException;
import com.common.powertech.xml.Login_Class;

public class PULLParse_Login {

	private static String rspcod,rspmsg,user_set_key,user_set_values,system_set_key,system_set_values;

	public static List<Login_Class> getLoginList(InputStream is) throws Exception{
		String enel_id1=null,enel_name1=null,enel_id2=null,enel_name2=null,enel_id3=null,enel_name3=null,enel_id4=null,enel_name4=null,PRDTYPEALL=null,PHONEAMOUNTCONFIG=null;
		Login_Class login=new Login_Class();
		List<Login_Class> list_login_class=new ArrayList<Login_Class>();
		List<String> list_menu_id=new ArrayList<String>();
		List<String> list_menu_nam=new ArrayList<String>();
		List<String> list_menu_en=new ArrayList<String>();
		List<String> list_menu_fr=new ArrayList<String>();
		Map<String, String> list_enel_grp1=new HashMap<String, String>();
		Map<String, String> list_enel_grp2=new HashMap<String, String>();
		Map<String, String> list_enel_grp3=new HashMap<String, String>();
		Map<String, String> list_enel_grp4=new HashMap<String, String>();
		Map<String, String> user_set=new HashMap<String, String>();
		Map<String, String> system_set=new HashMap<String, String>();
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
                if("KEY".equals(pullParser.getName())){
                	login.setKEY(pullParser.nextText());
                }
                if("SESSION_ID".equals(pullParser.getName())){
                	login.setSESSION_ID(pullParser.nextText());
                }
                if("LAW_NAME".equals(pullParser.getName())){
                	login.setLAW_NAME(pullParser.nextText());
                }
                if("OPER_NAME".equals(pullParser.getName())){
                 	login.setOPER_NAME(pullParser.nextText());
                }
                if("CASH_AC_BAL".equals(pullParser.getName())){
                	login.setCASH_AC_BAL(pullParser.nextText());
                }
                if("BUY_ELE_WAY".equals(pullParser.getName())){
                	login.setBUY_ELE_WAY(pullParser.nextText());
                }
                if("PAY_PWD".equals(pullParser.getName())){
                	login.setPAY_PWD(pullParser.nextText());
                }
                if("OPER_LIST".equals(pullParser.getName())){
                	login.setOPER_LIST(pullParser.nextText());
                }
                if("MENU_ID".equals(pullParser.getName())){
                	list_menu_id.add(pullParser.nextText());
                }
                if("MENU_NAM".equals(pullParser.getName())){
                	list_menu_nam.add(pullParser.nextText());
                }
                if("MENU_NAM_EN".equals(pullParser.getName())){
                	list_menu_en.add(pullParser.nextText());
                }
                if("MENU_NAM_FR".equals(pullParser.getName())){
                	list_menu_fr.add(pullParser.nextText());
                }   
                if("ENEL_ID1".equals(pullParser.getName())){
                	enel_id1=pullParser.nextText();
                }
                if("ENEL_NAME1".equals(pullParser.getName())){
                	enel_name1=pullParser.nextText();
                }
                if(enel_id1!=null && enel_name1!=null){
                	list_enel_grp1.put(enel_id1, enel_name1);
                } 
                if("ENEL_ID2".equals(pullParser.getName())){
                	enel_id2=pullParser.nextText();
                }
                if("ENEL_NAME2".equals(pullParser.getName())){
                	enel_name2=pullParser.nextText();
                }
                if(enel_id2!=null && enel_name2!=null){
                	list_enel_grp2.put(enel_id2, enel_name2);
                } 
                if("ENEL_ID3".equals(pullParser.getName())){
                	enel_id3=pullParser.nextText();
                }
                if("ENEL_NAME3".equals(pullParser.getName())){
                	enel_name3=pullParser.nextText();
                }
                if(enel_id3!=null && enel_name3!=null){
                	list_enel_grp3.put(enel_id3, enel_name3);
                } 
                if("ENEL_ID4".equals(pullParser.getName())){
                	enel_id4=pullParser.nextText();
                }
                if("ENEL_NAME4".equals(pullParser.getName())){
                	enel_name4=pullParser.nextText();
                }
                if(enel_id4!=null && enel_name4!=null){
                	list_enel_grp4.put(enel_id4, enel_name4);
                } 
                if("PRDTYPEALL".equals(pullParser.getName())){
                	login.setPRDTYPEALL(pullParser.nextText());
                }

                if("PHONEAMOUNTCONFIG".equals(pullParser.getName())){
                	login.setPHONEAMOUNTCONFIG(pullParser.nextText());
                }
                
                
                if("U_SET_NAME".equals(pullParser.getName())){
                	user_set_key=pullParser.nextText();
                }
                if("U_SET_STR".equals(pullParser.getName())){
                	user_set_values=pullParser.nextText();
                }
                if(user_set_key!=null && user_set_values!=null){
                	user_set.put(user_set_key, user_set_values);
                }                	
                if("S_SET_NAME".equals(pullParser.getName())){
                	system_set_key=pullParser.nextText();
                }
                if("S_SET_STR".equals(pullParser.getName())){
                	system_set_values=pullParser.nextText();
                }
                if(system_set_key!=null && system_set_values!=null){
                	system_set.put(system_set_key, system_set_values);
                }               	
                break;
                
			case XmlPullParser.END_TAG:
				if("ROOT".equals(pullParser.getName())){
					if(list_menu_id!=null && list_menu_id.size()!=0){
						login.setMENU_ID(list_menu_id);
					}					
					if(list_menu_nam!=null && list_menu_nam.size()!=0){
						login.setMENU_NAME(list_menu_nam);
					}					
					if(list_menu_en!=null && list_menu_en.size()!=0){
						login.setMENU_NAME_EN(list_menu_en);
					}					
					if(list_menu_fr!=null && list_menu_fr.size()!=0){
						login.setMENU_NAME_FR(list_menu_fr);
					}
					if(list_enel_grp1!=null && list_enel_grp1.size()!=0){
						login.setENEL_GRP1(list_enel_grp1);
					}	
					if(list_enel_grp2!=null && list_enel_grp2.size()!=0){
						login.setENEL_GRP2(list_enel_grp2);
					}	
					if(list_enel_grp3!=null && list_enel_grp3.size()!=0){
						login.setENEL_GRP3(list_enel_grp3);
					}	
					if(list_enel_grp4!=null && list_enel_grp4.size()!=0){
						login.setENEL_GRP4(list_enel_grp4);
					}	
					if(user_set.containsKey("DISPLAY")){
						login.setDISPLAY(user_set.get("DISPLAY"));
					}
					if(user_set.containsKey("VOICE")){
						login.setVOICE(user_set.get("VOICE"));
					}
					if(user_set.containsKey("LANGUAGE")){
						String language="";
						if(user_set.get("LANGUAGE").equals("1"))
							language="zh";
						else if (user_set.get("LANGUAGE").equals("2"))
							language="en";
						else if (user_set.get("LANGUAGE").equals("3"))
							language="fr";
						login.setLANGUAGE(language);
					}
					if(user_set.containsKey("LOCKTIME")){
						login.setLOCKTIME(user_set.get("LOCKTIME"));
					}
//					if(system_set.containsKey("ZIP")){
//						login.setZIP(system_set.get("ZIP"));
//					}
					if(system_set.containsKey("READ4428")){
						login.setREAD4428(system_set.get("READ4428"));
					}
					if(system_set.containsKey("READ4442")){
						login.setREAD4442(system_set.get("READ4442"));
					}
					if(system_set.containsKey("MCARD")){
						login.setMCARD(system_set.get("MCARD"));
					}
					if(system_set.containsKey("B4")){
						login.setB4(system_set.get("B4"));
					}
					if(system_set.containsKey("De")){
						login.setDe(system_set.get("De"));
					}
					if(system_set.containsKey("B9")){
						login.setB9(system_set.get("B9"));
					}
					if(system_set.containsKey("LASERH")){						
						login.setLASERH(system_set.get("LASERH"));
					}
					if(system_set.containsKey("PNO")){
						login.setPNO(system_set.get("PNO"));
					}
					if(system_set.containsKey("MINRECHARGE")){
						login.setMINRECHARGE(system_set.get("MINRECHARGE"));
					}
					list_login_class.add(login);
					login=null;
					rspcod=null;
					rspmsg=null;
				}
				break;
			}
			event=pullParser.next();
		}
		return list_login_class;
	}
}
