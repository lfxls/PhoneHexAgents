package com.common.powertech.bussiness;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yeqw on 2015/10/15.
 */
public class PULLParse_Income_Query {

    private static String rspcod, rspmsg, tolcnt;

    public static List<BillIncome_Class> getBIList(InputStream is) throws Exception{

        BillIncome_Class bi = null;
        List<BillIncome_Class> bis = null;
        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(is,"UTF-8");
        int event = pullParser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT){

            switch (event){
                case XmlPullParser.START_DOCUMENT:
                    bis = new ArrayList<BillIncome_Class>();
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
                        bi = new BillIncome_Class();
                    }
                    if( bi != null ){

                        if( "DATETIME".equals( pullParser.getName() ) ){
                            bi.Set_DATETIME( pullParser.nextText() );
                        }

                        if( "PRDORDNO".equals( pullParser.getName() ) ){
                            bi.Set_PRDORDNO( pullParser.nextText() );
                        }

                        if( "TXNTYP".equals( pullParser.getName() ) ){
                            bi.Set_TXNTYP( pullParser.nextText() );
                        }

                        if( "OUT".equals( pullParser.getName() ) ){
                            bi.Set_OUT( pullParser.nextText() );
                        }

                        if( "IN".equals( pullParser.getName() ) ){
                            bi.Set_IN( pullParser.nextText() );
                        }

                        if( "BANLANCE".equals( pullParser.getName() ) ){
                            bi.Set_BANLANCE( pullParser.nextText() );
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
