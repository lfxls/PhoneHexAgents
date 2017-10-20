package com.common.powertech.bussiness;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 日结查询返回XML解析
 * Created by yeqw on 2015/10/15.
 */
public class PULLParse_Transfer_Query {

    private static String rspcod, rspmsg, tolcnt;

    public static List<Transfer_Class> getBDList(InputStream is) throws Exception{

        Transfer_Class bd = null;
        List<Transfer_Class> bds = null;
        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(is,"UTF-8");
        int event = pullParser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT){

            switch (event){
                case XmlPullParser.START_DOCUMENT:
                    bds = new ArrayList<Transfer_Class>();
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
                        bd = new Transfer_Class();
                    }

                    if( bd != null ){

                        if( "PRDORDNO".equals( pullParser.getName() ) ){
                            bd.setPRDORDNO( pullParser.nextText() );
                        }

                        if( "ORDSTATUS".equals( pullParser.getName() ) ){
                            bd.setORDSTATUS( pullParser.nextText() );
                        }

                        if( "TOKEN_TYPE".equals( pullParser.getName() ) ){
                            bd.setTOKEN_TYPE( pullParser.nextText() );
                        }

                        if( "TOKEN_USER".equals(pullParser.getName())){
                            bd.setTOKEN_USER( pullParser.nextText() );
                        }

                        if( "TRANSFER_AMT".equals( pullParser.getName() ) ){
                            bd.setTRANSFER_AMT( pullParser.nextText() );
                        }
                        
                        if( "ORDERTIME".equals( pullParser.getName() ) ){
                            bd.setORDERTIME( pullParser.nextText() );
                        }
                        
                        if( "PIN_PHONE".equals( pullParser.getName() ) ){
                            bd.setPIN_PHONE( pullParser.nextText() );
                        }

                    }

                    break;

                case XmlPullParser.END_TAG:
                    if("STUDENT".equals( pullParser.getName() ) ){
                        bds.add(bd);
                        bd = null;
                    }
                    break;
            }
            event = pullParser.next();
        }
        return bds;
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
