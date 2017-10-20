package com.common.powertech.bussiness;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yeqw on 2015/10/15.
 */
public class PULLParse_Vending_Query {

    private static String rspcod, rspmsg, tolcnt;

    public static List<BillVending_Class> getBIList(InputStream is) throws Exception{

        BillVending_Class bi = null;
        List<BillVending_Class> bis = null;
        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(is,"UTF-8");
        int event = pullParser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT){

            switch (event){
                case XmlPullParser.START_DOCUMENT:
                    bis = new ArrayList<BillVending_Class>();
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
                        bi = new BillVending_Class();
                    }
                    if( bi != null ){
                    	//PRDORDNO,ORDSTAUS,ORDERTIME, OPERATOR_ID, METER_NO, TOKEN;
                    	
          
                        if( "PRDORDNO".equals( pullParser.getName() ) ){
                            bi.setPRDORDNO( pullParser.nextText() );
                        }

                        if( "ORDSTAUS".equals( pullParser.getName() ) ){
                            bi.setORDSTAUS( pullParser.nextText() );
                        }

                        if( "ORDERTIME".equals( pullParser.getName() ) ){
                            bi.setORDERTIME( pullParser.nextText() );
                        }

                        if( "OPERATOR_ID".equals( pullParser.getName() ) ){
                            bi.setOPERATOR_ID( pullParser.nextText() );
                        }

                        if( "METER_NO".equals( pullParser.getName() ) ){
                            bi.setMETER_NO( pullParser.nextText() );
                        }

                        if( "TOKEN".equals( pullParser.getName() ) ){
                            bi.setTOKEN( pullParser.nextText() );
                        }
                        
                        if( "ORDAMT_FMT".equals( pullParser.getName() ) ){
                            bi.setORDAMT_FMT( pullParser.nextText() );
                        }
                        
                        if( "ENEL_NAME".equals( pullParser.getName() ) ){
                            bi.setENEL_NAME( pullParser.nextText() );
                        }
                        
                        if( "ENEL_ID".equals( pullParser.getName() ) ){
                            bi.setENEL_ID( pullParser.nextText() );
                        }
                        
                        if( "PAY_METHOD".equals( pullParser.getName() ) ){
                            bi.setPAY_METHOD( pullParser.nextText() );
                        }
                        
                        if( "INPUT_AMT".equals( pullParser.getName() ) ){
                            bi.setINPUT_AMT( pullParser.nextText() );
                        }
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if("STUDENT".equals( pullParser.getName() ) ){
                        bis.add(bi);
                        bi = null;
                    }
                    break;
            }
            event = pullParser.next();
        }
        return bis;
    }

    public static String getRspcod(){
        return rspcod;
    }

    public static String getRspmsg(){
        return rspmsg;
    }

    public static String getTolcnt(){
        return tolcnt;
    }
}
