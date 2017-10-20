package printUtils;

import java.util.List;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.RemoteException;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;
import com.gprinter.command.GpUtils;
import com.gprinter.command.LabelCommand;
import com.gprinter.command.EscCommand.CODEPAGE;
import com.gprinter.command.EscCommand.ENABLE;
import com.gprinter.command.EscCommand.FONT;
import com.gprinter.command.EscCommand.HRI_POSITION;
import com.gprinter.command.EscCommand.JUSTIFICATION;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class gprinter implements IBluePrinter{
	 private static HashMap a = new HashMap();
	 private List printList = new ArrayList();
	 public static GpService nGpService = null;
	 private int mPrinterIndex = 0;
	 
	 public static HashMap getA(){
		 return a;
	 }
	  public static void setTemplet(String paramString, Map iterator)
	  {
	    if ((paramString == null) || (iterator == null))
	      return;
	    HashMap localHashMap = new HashMap();
	    Iterator paramMap =  iterator.entrySet().iterator();
	    while (paramMap.hasNext())
	    {
	      Map.Entry localEntry = (Map.Entry)paramMap.next();
	      StyleConfig localStyleConfig1 = new StyleConfig();
	      String[] arrayOfString = ((String)localEntry.getValue()).split("\\|");
	      for (int i = 0; i < arrayOfString.length; i++)
	      {
	        StyleConfig localStyleConfig2 = localStyleConfig1;
	        String str;
	        if (((str = arrayOfString[i]) != null) && (localStyleConfig2 == null))
	          continue;
	        if (str.equals("TP"))
	        {
	          localStyleConfig2.mode = 1;
	        }
	        else if (str.equals("TM"))
	        {
	          localStyleConfig2.mode = 2;
	        }
	        else if (str.equals("TM2"))
	        {
	          localStyleConfig2.mode = 3;
	        }
	        else if (str.startsWith("DQ_"))
	        {
	          if (str.regionMatches(3, "AM", 0, 2))
	            localStyleConfig2.align = 1;
	          else if (str.regionMatches(3, "AR", 0, 2))
	            localStyleConfig2.align = 2;
	          else
	            localStyleConfig2.align = 0;
	        }
	        else if (str.startsWith("FT_"))
	        {
	          if (str.regionMatches(3, "1", 0, 1))
	            localStyleConfig2.fontSize = 1;
	          else
	            localStyleConfig2.fontSize = 2;
	        }
	        else if (str.startsWith("WM_"))
	        {
	          if (str.regionMatches(3, "2", 0, 1))
	            localStyleConfig2.multiWidth = 2;
	          else
	            localStyleConfig2.multiWidth = 1;
	        }
	        else if (str.startsWith("HM_"))
	        {
	          if (str.regionMatches(3, "2", 0, 1))
	            localStyleConfig2.multiHeight = 2;
	          else
	            localStyleConfig2.multiHeight = 1;
	        }
	        else if (str.startsWith("FB_"))
	        {
	          if (str.regionMatches(3, "T", 0, 1))
	            localStyleConfig2.highLight = 1;
	          else
	            localStyleConfig2.highLight = 0;
	        }
	        else
	        {
	          int j;
	          if (str.startsWith("HD_"))
	          {
	            if ((j = Integer.valueOf(str.substring(3)).intValue()) > 12)
	              j = 12;
	            localStyleConfig2.gray = j;
	          }
	          else if (str.startsWith("JJ_"))
	          {
	            if ((j = Integer.valueOf(str.substring(3)).intValue()) > 255)
	              j = 255;
	            localStyleConfig2.lineSpace = j;
	          }
	          else
	          {
	            if (!str.startsWith("FP_"))
	              continue;
	            if ((j = Integer.valueOf(str.substring(3)).intValue()) > 255)
	              j = 255;
	            localStyleConfig2.feed = j;
	          }
	        }
	      }
	      localHashMap.put((String)localEntry.getKey(), localStyleConfig1);
	    }
	    a.put(paramString, localHashMap);
	  }
	  public static void setTemplet(String paramString)
	  {
	    if (paramString == null)
	      throw new NullPointerException();
	    Object localObject = DocumentBuilderFactory.newInstance();
	    try
	    {
	      localObject = ((DocumentBuilderFactory)localObject).newDocumentBuilder();
	    }
	    catch (ParserConfigurationException localParserConfigurationException)
	    {
	      ((Throwable) (localObject = localParserConfigurationException)).printStackTrace();
	      return;
	    }
	    try
	    {
	    	 localObject = ((DocumentBuilder)localObject).parse(new ByteArrayInputStream(paramString.getBytes()));
	    }
	    catch (SAXException localSAXException)
	    {
	      ((Throwable) (localObject = localSAXException)).printStackTrace();
	      return;
	    }
	    catch (IOException localIOException)
	    {
	      ((Throwable) (localObject = localIOException)).printStackTrace();
	      return;
	    }
//	    localObject = new HashMap();
	    String str1 = "2";
	    localObject= ((Document) localObject).getDocumentElement();
	    Element param =(Element) localObject;
	    int i =  param.getChildNodes().getLength();
	    NodeList nodeList = param.getChildNodes();
	    HashMap localmap=new HashMap();
	    for (int j = 0; j < i; j++)
	    {
	      Node localNode;
	      if ((localNode = nodeList.item(j)).getNodeType() != 1)
	        continue;
	      String str2 = localNode.getNodeName();
	      if (str2.toUpperCase().equals("TYPE_T"))
	        str1 = localNode.getFirstChild().getNodeValue();
	      else
	        localmap.put(str2, localNode.getFirstChild().getNodeValue());
	    }
	    setTemplet(str1, (Map)localmap);
	  }

	@Override
	public void printXML(String paramString) {
		//<TICKET><TYPE_T>1</TYPE_T><TITLE_T>Toll bill</TITLE_T><CONTENT_T>Sinagel</CONTENT_T><CONTENT_T>DIANE MALLE</CONTENT_T><CONTENT_T>Meter No.777</CONTENT_T><CONTENT_T>Order No.D016080300313298</CONTENT_T><CONTENT_T>Time:2016-08-03 11:56:59</CONTENT_T><CONTENT_T>Agent:1234567890 1234567890 1234567890 1234567890 1234567890 12345</CONTENT_T><CONTENT_T>Tax No.test123456789</CONTENT_T><CONTENT_T>Operator:adasd</CONTENT_T><CONTENT_T>Currency：senegal</CONTENT_T><CONTENT_T>Total Amount:3000.00</CONTENT_T><CONTENT_T>Energy Amount:2800.00</CONTENT_T><CONTENT_T>Electricity Amount:90.00</CONTENT_T><CONTENT_T>Service Fee:200.00</CONTENT_T><CONTENT_T>Tax Amount:10.00</CONTENT_T><CONTENT_T>Debt Amount:30.00</CONTENT_T><CONTENT_T>Energy:11</CONTENT_T><CONTENT_T>TOKEN:</CONTENT_T><TOKEN_T>1968 8095 4594 0931 9929</TOKEN_T><TM2>HexpayD016080300313298</TM2><FPSPACE/><TM>D016080300313298</TM><FP/></TICKET>
		// TODO Auto-generated method stub

	    if (paramString == null)
	      throw new NullPointerException();
	    Object localObject1 = DocumentBuilderFactory.newInstance();
	    try
	    {
	      localObject1 = ((DocumentBuilderFactory)localObject1).newDocumentBuilder();
	    }
	    catch (ParserConfigurationException localParserConfigurationException)
	    {
	      ((Throwable) (localObject1 = localParserConfigurationException)).printStackTrace();
	      return;
	    }
	    try
	    {
	    	localObject1 = ((DocumentBuilder)localObject1).parse(new ByteArrayInputStream(paramString.getBytes()));
	    }
	    catch (SAXException localSAXException)
	    {
	      ((Throwable) (localObject1 = localSAXException)).printStackTrace();
	      return;
	    }
	    catch (IOException localIOException)
	    {
	      ((Throwable) (localObject1 = localIOException)).printStackTrace();
	      return;
	    }
	    Element root=((Document) localObject1).getDocumentElement();
	    NodeList nodeList=root.getChildNodes();
	    HashMap localHashMap = null;
	    List paramList=new ArrayList();
	    String str1 = "2";
	    int j = nodeList.getLength();
	    for (int k = 0; k < j; k++)
	    {
	      Node localNode1;
	      int aaaa =  nodeList.item(k).getNodeType();
	      if ((localNode1 = nodeList.item(k)).getNodeType() != 1)
	        continue;
	      Object localObject2 = localNode1.getNodeName();
	      if (((String)localObject2).toUpperCase().equals("TYPE_T"))
	      {
	        str1 = localNode1.getFirstChild().getNodeValue();
	      }
	      else
	      {
	        int i;
	        if ((i = ((NodeList) (localObject2 = localNode1.getChildNodes())).getLength()) == 0)
	        {
	          (localHashMap = new HashMap()).put(localNode1.getNodeName(), "");
	          paramList.add(localHashMap);
	        }
	        else
	        {
	          for (int m = 0; m < i; m++)
	          {
	            Node localNode2;
	            localHashMap = new HashMap();
	            if ((localNode2 = ((NodeList)localObject2).item(m)).getNodeType() == 3)
	            {
	              if (m == i - 1)
	                localHashMap.put(localNode1.getNodeName(), localNode2.getNodeValue() + "\n");
	              else
	                localHashMap.put(localNode1.getNodeName(), localNode2.getNodeValue());
	            }
	            else
	            {
	              String str2;
	              if ((localNode2.getNodeType() == 1) && ((str2 = localNode2.getNodeName()).startsWith("INC_")))
	              {
	                localHashMap = new HashMap();
	                Log.i("Printer", str2.substring(4));
	                Log.i("Printer", localNode2.getFirstChild().getNodeValue());
	                if (m == i - 1)
	                  localHashMap.put(str2.substring(4), localNode2.getFirstChild().getNodeValue() + "\n");
	                else
	                  localHashMap.put(str2.substring(4), localNode2.getFirstChild().getNodeValue());
	              }
	            }
	            if (localHashMap == null)
	              continue;
	            paramList.add(localHashMap);
	            localHashMap = null;
	          }
	        }
	      }
	    }
	    print(str1, paramList);
	}

	//paramString1 类型为1；paramList1小票List
	@Override
	public void print(String paramString1, List paramList1) {
		// TODO Auto-generated method stub
		this.printList.clear();
	    if ((paramString1 == null) || (paramList1 == null))
	      return;
	    HashMap paramString;
		if ((paramString = (HashMap)a.get(paramString1)) == null)//paramString 模版赋值 
	      return;
	    Iterator localIterator2 = paramList1.iterator();
	    Map.Entry paramList=null;
	    while (localIterator2.hasNext())
	    {
	      Iterator localIterator1 = ((Map)localIterator2.next()).entrySet().iterator();
	      while (localIterator1.hasNext())
	      {
	        paramList = (Map.Entry)localIterator1.next();
	        StyleConfig localStyleConfig;
	        if ((localStyleConfig = (StyleConfig)paramString.get(paramList.getKey())) == null)
	          continue;
	        if (localStyleConfig.mode == 0||localStyleConfig.mode == 1||localStyleConfig.mode == 3)
	        {
	        	String localValue=(String) paramList.getValue();
	        	if (localStyleConfig.mode == 1||localStyleConfig.mode == 3)
		          {
		            if (((localValue = (String)paramList.getValue())).endsWith("\n"))
		            	localValue = localValue.replace("\n", "");
		          }
	        	if(paramList.getKey().equals("TOKEN_T")){
	        		localStyleConfig.bold=1;
	        	}
	          this.printList.add(new a(this, localValue, localStyleConfig));
	        }
	        else
	        {
	          Object localObject;
	          if (localStyleConfig.mode == 1)
	          {
	            if (((String) (localObject = (String)paramList.getValue())).endsWith("\n"))
	              localObject = new File(((String)localObject).subSequence(0, ((String)localObject).length() - 1).toString());
	            else
	              localObject = new File((String)localObject);
	            if (!((File)localObject).exists())
	              continue;
	            localObject = a(BitmapFactory.decodeFile((String)paramList.getValue()), localStyleConfig.align);
//	            if ((paramList = this.printList.size()) > 0)
//	            {
//	              paramList--;
//	              if (((paramList = (a)this.printList.get(paramList)).b.mode == 0) && (paramList.string.endsWith("\n")))
//	                paramList.string = paramList.string.substring(0, paramList.string.length() - 1);
//	            }
	            this.printList.add(new a(this, (Bitmap)localObject, localStyleConfig));
	          }
	          else if (localStyleConfig.mode == 2)
	          {
	            if (((String) (localObject = (String)paramList.getValue())).endsWith("\n"))
	              localObject = ((String)localObject).substring(0, ((String)localObject).length() - 1).toString();
	            else
	              localObject = localObject;
	            localObject = a(a((String)localObject, BarcodeFormat.CODE_128, 360, 64), localStyleConfig.align);
//	              if ((paramList = this.printList.size()) > 0)
//	              {
//	                paramList--;
//	                if (((paramList = (a)this.printList.get(paramList)).b.mode == 0) && (paramList.string.endsWith("\n")))
//	                  paramList.string = paramList.string.substring(0, paramList.string.length() - 1);
//	              }
	              this.printList.add(new a(this, (Bitmap)localObject, localStyleConfig));
	          }
	          else
	          {
	            if (localStyleConfig.mode != 3)
	              continue;
	            if (((String) (localObject = (String)paramList.getValue())).endsWith("\n"))
	              localObject = ((String)localObject).substring(0, ((String)localObject).length() - 1).toString();
	            else
	              localObject = localObject;
	            Bitmap param=null;
	              localObject = a((Bitmap) (localObject = Bitmap.createBitmap(param = a((String)localObject, BarcodeFormat.QR_CODE, 256, 256), 40, 40, param.getWidth() - 80, param.getHeight() - 80)), localStyleConfig.align);
//	              if ((paramList = this.printList.size()) > 0)
//	              {
//	                paramList--;
//	                if (((paramList = (a)this.printList.get(paramList)).b.mode == 0) && (paramList.string.endsWith("\n")))
//	                  paramList.string = paramList.string.substring(0, paramList.string.length() - 1);
//	              }
	              this.printList.add(new a(this, (Bitmap)localObject, localStyleConfig));
	          }
	        }
	      }
	    }
	}
	  private static Bitmap a(String paramString1, BarcodeFormat paramBarcodeFormat1, int paramInt1, int paramInt2)
	  {
		  BitMatrix  paramString=null;
	    int paramBarcodeFormat = paramInt2;
		try {
			paramBarcodeFormat = (paramString = new MultiFormatWriter().encode(paramString1, paramBarcodeFormat1, paramInt1, paramInt2)).getWidth();
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    paramInt1 = paramString.getHeight();
	    int[] paramInt = new int[paramBarcodeFormat * paramInt1];
	    int localObject;
	    for (localObject = 0; localObject < paramInt1; localObject++)
	      for (int localBarcodeFormat = 0; localBarcodeFormat < paramBarcodeFormat; localBarcodeFormat++)
	        if (paramString.get(localBarcodeFormat, localObject))
	          paramInt[(localObject * paramBarcodeFormat + localBarcodeFormat)] = -16777216;
	        else
	          paramInt[(localObject * paramBarcodeFormat + localBarcodeFormat)] = -1;
	    Bitmap reObject;
		((reObject = Bitmap.createBitmap(paramBarcodeFormat, paramInt1, Bitmap.Config.ARGB_8888))).setPixels(paramInt, 0, paramBarcodeFormat, 0, 0, paramBarcodeFormat, paramInt1);
	    return reObject;
	  }
	 private static Bitmap a(Bitmap paramBitmap, int paramInt1)
	  {
	    if (paramBitmap == null)
	      return null;
	    int i = paramBitmap.getWidth();
	    int j = paramBitmap.getHeight();
	    int k = 0;
	    int n = 0;
	    int m;
	    if (paramInt1 == 1)
	    {
	      k = (384 - i) / 2;
	      if ((n = (i += k) % 8) != 0)
	        i += 8 - n;
	    }
	    else if (paramInt1 == 2)
	    {
	      m = 384 - i;
	      i = 384;
	    }
	    else if ((n = i % 8) != 0)
	    {
	      i += 8 - n;
	    }
	    Bitmap paramInt = Bitmap.createBitmap(i, j, paramBitmap.getConfig());
	    Paint localPaint;
	    (localPaint = new Paint()).setColor(-1);
	    Canvas localCanvas;
	    (localCanvas = new Canvas(paramInt)).drawRect(0.0F, 0.0F, i, j, localPaint);
	    localCanvas.drawBitmap(paramBitmap, 0.0F, 0.0F, null);
	    return paramInt;
	  }

	@Override
	public String commitOperation() {

	        EscCommand esc = new EscCommand();
	        esc.addInitializePrinter();
	        esc.addPrintAndFeedLines((byte) 3);
	      Iterator localIterator = this.printList.iterator();
	      Object localObject2;
	      while (localIterator.hasNext())
	      {
	        a locala;
	        if (((locala = (a)localIterator.next()).b.mode == 1) || (locala.b.mode == 2) || (locala.b.mode == 3)){
	        	esc.addSelectJustification(JUSTIFICATION.CENTER);
	        	JUSTIFICATION direction=JUSTIFICATION.CENTER;
	        	if(locala.b.align==1)
	        		direction=JUSTIFICATION.CENTER;
	        	else if(locala.b.align==2)
	        		direction=JUSTIFICATION.RIGHT;
	        	else
	        		direction=JUSTIFICATION.LEFT;
	        	esc.addSelectJustification(direction);// 设置打印对齐方式
	        	if(locala.b.mode==2){
	        		/* 打印一维条码 */
		            esc.addSelectPrintingPositionForHRICharacters(HRI_POSITION.BELOW);//
		            // 设置条码可识别字符位置在条码下方
//		            esc.addSetBarcodeHeight((byte) 100); // 设置条码高度
//		            esc.addSetBarcodeWidth((byte) 2); // 设置条码单元宽度
//		            esc.addCODE128(esc.genCodeB(locala.string)); // 打印Code128码
		            esc.addRastBitImage(locala.bitmap, 300, 300);
		            esc.addPrintAndLineFeed();
	        	}else if(locala.b.mode==3){
	        		/*
	                 * QRCode命令打印 此命令只在支持QRCode命令打印的机型才能使用。 在不支持二维码指令打印的机型上，则需要发送二维条码图片
	        		 */
	                esc.addSelectErrorCorrectionLevelForQRCode((byte) 0x33); // 设置纠错等级
	                esc.addSelectSizeOfModuleForQRCode((byte) 9);// 设置qrcode模块大小
	                esc.addStoreQRCodeData(locala.string);// 设置qrcode内容
	                esc.addPrintQRCode();// 打印QRCode
//	                esc.addRastBitImage(locala.bitmap, 300, 300);
	                esc.addPrintAndLineFeed();
	        	}
	            esc.addPrintAndFeedPaper((byte) locala.b.feed);//走纸
	        	
	        	
		           /* ThermalPrinter.setAlgin(0);
		            ThermalPrinter.setGray(locala.b.gray);
		            ThermalPrinter.printLogo(locala.bitmap);
		            if (locala.b.feed <= 0)
		              continue;
		            ThermalPrinter.walkPaper(locala.b.feed);*/
	        	}
	        else{
	            esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF);// 设置为倍高倍宽
	            FONT font=FONT.FONTB;//字体
	            ENABLE emphasized=ENABLE.OFF;//是否加粗
	            ENABLE doubleheight=ENABLE.OFF;//倍高
	            ENABLE doublewidth=ENABLE.OFF;//倍宽
	            ENABLE underline=ENABLE.OFF;//
	            if(locala.b.bold==1)
	            	emphasized=ENABLE.ON;
	        	else
	        		emphasized=ENABLE.OFF;
	        	if(locala.b.fontSize==1)
	        		font=FONT.FONTB;
	        	else
	        		font=FONT.FONTA;
	        	
	        	if(locala.b.multiHeight==1)
	        		doubleheight=ENABLE.OFF;
	        	else
	        		doubleheight=ENABLE.ON;
	        	
	        	if(locala.b.multiWidth==1)
	        		doublewidth=ENABLE.OFF;
	        	else
	        		doublewidth=ENABLE.ON;
	        	esc.addSelectPrintModes(font, emphasized, doubleheight, doublewidth, underline);// 设置为倍高倍宽
	        	
	        	esc.addSelectJustification(JUSTIFICATION.LEFT);
	        	JUSTIFICATION direction=JUSTIFICATION.LEFT;
	        	if(locala.b.align==1)
	        		direction=JUSTIFICATION.CENTER;
	        	else if(locala.b.align==1)
	        		direction=JUSTIFICATION.RIGHT;
	        	else
	        		direction=JUSTIFICATION.LEFT;
	        	esc.addSelectJustification(direction);// 设置打印对齐方式
	        	esc.addSetLineSpacing((byte) (locala.b.lineSpace*2));//行间距
	        	if(!locala.string.equals("")){
		            esc.addText(locala.string); // 打印文字
	        	}
	            esc.addPrintAndFeedPaper((byte)(locala.b.feed*2.5));//走纸
	        	
	        }
	      }
	      
	        Vector<Byte> datas = esc.getCommand(); // 发送数据
	        byte[] bytes = GpUtils.ByteTo_byte(datas);
	        String sss = Base64.encodeToString(bytes, Base64.DEFAULT);
	        int rs;
	        String error = null;
	        try {
	            rs = nGpService.sendEscCommand(mPrinterIndex, sss);
	            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rs];
	            if (r != GpCom.ERROR_CODE.SUCCESS) {
	            	error=GpCom.getErrorText(r);
	            }else{
	            	error="Print Success!";
	            }
	        } catch (RemoteException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    return error;
	}

	
	
	 void sendReceipt() {
	        EscCommand esc = new EscCommand();
	        esc.addInitializePrinter();
	        esc.addPrintAndFeedLines((byte) 3);
	        esc.addSelectJustification(JUSTIFICATION.CENTER);// 设置打印居中
	        esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.ON, ENABLE.ON, ENABLE.OFF);// 设置为倍高倍宽
	        esc.addText("Sample\n"); // 打印文字
	        esc.addPrintAndLineFeed();

			/* 打印文字 */
	        esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF);// 取消倍高倍宽
	        esc.addSelectJustification(JUSTIFICATION.LEFT);// 设置打印左对齐
	        esc.addText("Print text\n"); // 打印文字
	        esc.addText("Welcome to use SMARNET printer!\n"); // 打印文字

			/* 打印繁体中文 需要打印机支持繁体字库 */
	        esc.addCancelKanjiMode();
	        esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.ON, ENABLE.ON, ENABLE.OFF);
	        String message = " أنا في الصين ";
	        // esc.addText(message,"BIG5");
//	        esc.addText(message, "UTF-8");
	        esc.addSelectCodePage(CODEPAGE.ARABIC);
	        esc.addArabicText(message);
	        esc.addPrintAndLineFeed();
	        esc.addSelectKanjiMode();
			/* 绝对位置 具体详细信息请查看GP58编程手册 */
	        esc.addText("智汇");
	        esc.addSetHorAndVerMotionUnits((byte) 7, (byte) 0);
	        esc.addSetAbsolutePrintPosition((short) 6);
	        esc.addText("网络");
	        esc.addSetAbsolutePrintPosition((short) 10);
	        esc.addText("设备");
	        esc.addPrintAndLineFeed();

			/* 打印图片 */
	        // esc.addText("Print bitmap!\n"); // 打印文字
	        // Bitmap b = BitmapFactory.decodeResource(getResources(),
	        // R.drawable.Gprinter);
	        // esc.addRastBitImage(b, b.getWidth(), 0); // 打印图片

			/* 打印一维条码 */
	        esc.addText("Print code128\n"); // 打印文字
	        esc.addSelectPrintingPositionForHRICharacters(HRI_POSITION.BELOW);//
	        // 设置条码可识别字符位置在条码下方
	        esc.addSetBarcodeHeight((byte) 60); // 设置条码高度为60点
	        esc.addSetBarcodeWidth((byte) 1); // 设置条码单元宽度为1
	        esc.addCODE128(esc.genCodeB("SMARNET")); // 打印Code128码
	        esc.addPrintAndLineFeed();

			/*
	         * QRCode命令打印 此命令只在支持QRCode命令打印的机型才能使用。 在不支持二维码指令打印的机型上，则需要发送二维条码图片
			 */
	        esc.addText("Print QRcode\n"); // 打印文字
	        esc.addSelectErrorCorrectionLevelForQRCode((byte) 0x31); // 设置纠错等级
	        esc.addSelectSizeOfModuleForQRCode((byte) 3);// 设置qrcode模块大小
	        esc.addStoreQRCodeData("www.smarnet.cc");// 设置qrcode内容
	        esc.addPrintQRCode();// 打印QRCode
	        esc.addPrintAndLineFeed();

			/* 打印文字 */
	        esc.addSelectJustification(JUSTIFICATION.CENTER);// 设置打印左对齐
	        esc.addText("Completed!\r\n"); // 打印结束
	        esc.addGeneratePlus(LabelCommand.FOOT.F5, (byte) 255, (byte) 255);
	        // esc.addGeneratePluseAtRealtime(LabelCommand.FOOT.F2, (byte) 8);

	        esc.addPrintAndFeedLines((byte) 8);

	        Vector<Byte> datas = esc.getCommand(); // 发送数据
	        byte[] bytes = GpUtils.ByteTo_byte(datas);
	        String sss = Base64.encodeToString(bytes, Base64.DEFAULT);
	        int rs;
	        try {
	            rs = nGpService.sendEscCommand(mPrinterIndex, sss);
//	            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rs];
//	            if (r != GpCom.ERROR_CODE.SUCCESS) {
//	                Toast.makeText(getApplicationContext(), GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
//	            }
	        } catch (RemoteException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    }
}
