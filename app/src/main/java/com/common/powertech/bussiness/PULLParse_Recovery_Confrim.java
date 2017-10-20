package com.common.powertech.bussiness;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

/**
 * Created by yeqw on 2015/10/24.
 */
public class PULLParse_Recovery_Confrim {

    private static String rspcod, rspmsg;

    public static void parse(InputStream is) throws Exception{

        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(is,"UTF-8");
        int event = pullParser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT){

            switch (event){
                case XmlPullParser.START_DOCUMENT:
                    break;

                case XmlPullParser.START_TAG:

                    if( "RSPCOD".equals( pullParser.getName() ) ){
                        rspcod = pullParser.nextText();
                    }

                    if( "RSPMSG".equals( pullParser.getName() ) ){
                        rspmsg = pullParser.nextText();
                    }

                    break;
            }
            event = pullParser.next();
        }
    }

    public static String getRspcod(){
        return rspcod;
    }

    public static String getRspmsg(){
        return rspmsg;
    }

}
