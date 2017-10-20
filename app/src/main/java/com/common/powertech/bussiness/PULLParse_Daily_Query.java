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
public class PULLParse_Daily_Query {

    private static String rspcod, rspmsg, tolcnt;

    public static List<BillDaily_Class> getBDList(InputStream is) throws Exception{

        BillDaily_Class bd = null;
        List<BillDaily_Class> bds = null;
        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(is,"UTF-8");
        int event = pullParser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT){

            switch (event){
                case XmlPullParser.START_DOCUMENT:
                    bds = new ArrayList<BillDaily_Class>();
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
                        bd = new BillDaily_Class();
                    }

                    if( bd != null ){

                        if( "TOF_DATE".equals( pullParser.getName() ) ){
                            bd.Set_TOF_DATE( pullParser.nextText() );
                        }

                        if( "TOF_NO".equals( pullParser.getName() ) ){
                            bd.Set_TOF_NO( pullParser.nextText() );
                        }

                        if( "TOF_OPR".equals( pullParser.getName() ) ){
                            bd.Set_TOF_OPR( pullParser.nextText() );
                        }

                        if( "TOF_AMT".equals( pullParser.getName() ) ){
                            bd.Set_TOF_AMT( pullParser.nextText() );
                        }

                        if( "FTA_STATUS".equals( pullParser.getName() ) ){
                            bd.Set_FTA_STATUS( pullParser.nextText() );
                        }

                        if( "FTA_DEAL_OPR".equals( pullParser.getName() ) ){
                            bd.Set_FTA_DEAL_OPR( pullParser.nextText() );
                        }

                        if( "FTA_DEAL_DATE".equals( pullParser.getName() ) ){
                            bd.Set_FTA_DEAL_DATE( pullParser.nextText() );
                        }

                        if( "JSON".equals( pullParser.getName() ) ){
                            bd.Set_JSON( pullParser.nextText() );
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
