package com.common.powertech.webservice;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.common.powertech.PowertechApplication;
import com.common.powertech.param.GlobalParams;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class Client {
	static PowertechApplication app ;
    public static String ConnectServer(String interfaceName, String xmlData) throws Exception{
    	
		HttpTransportSE httpTransportSE=new HttpTransportSE("http://"+app.getSERVERADDRESS()+"/api/services/hexingws?wsdl", 30000);
		SoapSerializationEnvelope soapSerializationEnvelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
		SoapObject soapObject=new SoapObject("http://service.ws.tangdi/", "trans");
		soapObject.addProperty("arg0", interfaceName);
		soapObject.addProperty("arg1", xmlData);
		soapSerializationEnvelope.bodyOut=soapObject;
		httpTransportSE.call(null, soapSerializationEnvelope);
		if(soapSerializationEnvelope.getResponse()!=null){
			SoapObject result=(SoapObject) soapSerializationEnvelope.bodyIn;
			String detail=result.getProperty(0).toString();
			httpTransportSE.getServiceConnection().disconnect();
            return detail;
		}
        httpTransportSE.getServiceConnection().disconnect();
		return null;
    }

    public static void SendData(final String APIName, String Data, final Handler handler) {

        final Handler mhandler;

        final HttpTransportSE httpTransportSE=new HttpTransportSE("http://"+app.getSERVERADDRESS()+"/api/services/hexingws?wsdl",30000);
        final SoapSerializationEnvelope soapSerializationEnvelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
        SoapObject soapObject=new SoapObject("http://service.ws.tangdi/","trans");
        soapObject.addProperty("arg0",APIName);
        soapObject.addProperty("arg1",Data);
        Log.e("Send data:", Data);
        soapSerializationEnvelope.bodyOut=soapObject;

        mhandler = handler;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    httpTransportSE.call(null, soapSerializationEnvelope);
                    if(soapSerializationEnvelope.getResponse()!=null){
                        SoapObject result=(SoapObject)soapSerializationEnvelope.bodyIn;
                        Object detail1 =  (Object)result.getProperty(0);
                        String detail2 =  detail1.toString();
                        GlobalParams.RETURN_DATA = detail2;
                        Log.e("Connect Result:", detail2);
                        if(APIName.equals("PLogin")){

                            Thread.sleep(2000);
                            GlobalParams.SESSION_ID =  Parse_XML(detail2, "<SESSION_ID>","</SESSION_ID>");
                            GlobalParams.KEY =  Parse_XML(detail2, "<KEY>","</KEY>");

                        }
                        //服务器有返回
                        if(handler != null){

                            if( APIName.equals("PBillReversalApp") || APIName.equals("PBillDailyConfirm") ||  APIName.equals("PBillReprint")){
                                Message msg = handler.obtainMessage();
                                msg.what = 2;
                                handler.sendMessage(msg);
                            }else{

                                Message msg = handler.obtainMessage();
                                msg.what = 1;
                                handler.sendMessage(msg);
                            }
                        }

                    }else{

                        //联网失败
                        if(handler != null){
                            Message msg = handler.obtainMessage();
                            msg.what = 0;
                            handler.sendMessage(msg);
                        }
                    }

                    //httpTransportSE.getServiceConnection().disconnect();

                }catch (Exception e){
                    //联网失败
                    if(handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = 0;
                        handler.sendMessage(msg);
                    }
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    public static void SendDataNoThread(final String APIName, String Data, final Handler handler) {

        final Handler mhandler;

        final HttpTransportSE httpTransportSE=new HttpTransportSE("http://"+GlobalParams.SERVER_ADDRESS+"/api/services/hexingws?wsdl",30000);
        final SoapSerializationEnvelope soapSerializationEnvelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
        SoapObject soapObject=new SoapObject("http://service.ws.tangdi/","trans");
        soapObject.addProperty("arg0",APIName);
        soapObject.addProperty("arg1",Data);
        Log.e("Send data:", Data);
        soapSerializationEnvelope.bodyOut=soapObject;

        mhandler = handler;

                try {
                    httpTransportSE.call(null, soapSerializationEnvelope);
                    if(soapSerializationEnvelope.getResponse()!=null){
                        SoapObject result=(SoapObject)soapSerializationEnvelope.bodyIn;
                        Object detail1 =  (Object)result.getProperty(0);
                        String detail2 =  detail1.toString();
                        GlobalParams.RETURN_DATA = detail2;
                        Log.e("Connect Result:", detail2);
                        //服务器有返回
                        if(handler != null){
                            Message msg = handler.obtainMessage();
                            msg.what = 1;
                            handler.sendMessage(msg);
                        }

                    }else{

                        //联网失败
                        if(handler != null){
                            Message msg = handler.obtainMessage();
                            msg.what = 0;
                            handler.sendMessage(msg);
                        }
                    }
                }catch (Exception e){
                    //联网失败
                    if(handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = 0;
                        handler.sendMessage(msg);
                    }
                    e.printStackTrace();
                }
            }

    public static String Parse_XML(String str, String start, String end){
        String ret="";
        if(null == str){
            return "";
        }
        int st = str.indexOf(start);
        if(st<0){
            return "";
        }
        st+=start.length();
        int ed = str.indexOf(end);
        if(ed<0){
            return "";
        }
        ret = str.substring(st,ed);
        return ret;
    }
}
