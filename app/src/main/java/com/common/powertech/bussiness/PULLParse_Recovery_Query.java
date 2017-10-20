package com.common.powertech.bussiness;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yeqw on 2015/10/15.
 */
public class PULLParse_Recovery_Query {

    private static String rspcod, rspmsg, tolcnt;

    public static List<BillRecovery_Class> getBRList(InputStream is) throws Exception{
        BillRecovery_Class br = null;
        List<BillRecovery_Class> brs = null;
        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(is,"UTF-8");
        int event = pullParser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT){

            switch (event){
                case XmlPullParser.START_DOCUMENT:
                    brs = new ArrayList<BillRecovery_Class>();
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
                        br = new BillRecovery_Class();
                    }
                    if( br != null ){

                        if( "PRDORDNO".equals( pullParser.getName() ) ){
                            br.Set_PRDORDNO( pullParser.nextText() );
                        }

                        if( "ORDERTIME".equals( pullParser.getName() ) ){
                            br.Set_ORDERTIME( pullParser.nextText() );
                        }

                        if( "ORDAMT".equals( pullParser.getName() ) ){
                            br.Set_ORDAMT( pullParser.nextText() );
                        }

                        if( "BIZ_TYPE".equals( pullParser.getName() ) ){
                            br.Set_BIZ_TYPE( pullParser.nextText() );
                        }

                        if( "USER_NO".equals( pullParser.getName() ) ){
                            br.Set_USER_NO( pullParser.nextText() );
                        }

                        if( "ELEN_ID".equals( pullParser.getName() ) ){
                            br.Set_ELEN_ID( pullParser.nextText() );
                        }

                        if( "R_STATUS".equals( pullParser.getName() ) ){
                            br.Set_R_STATUS( pullParser.nextText() );
                        }
                    }
                    break;

                case XmlPullParser.END_TAG:
                        if("STUDENT".equals( pullParser.getName() ) ){
                            brs.add(br);
                            br = null;
                        }
                    break;
            }
            event = pullParser.next();
        }

        return brs;
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
