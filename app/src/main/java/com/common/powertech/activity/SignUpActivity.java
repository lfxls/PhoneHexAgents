package com.common.powertech.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.common.powertech.R;
import com.common.powertech.bussiness.Request_Signup_Confirm;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.webservice.Client;
import com.myDialog.CustomProgressDialog;
import com.myDialog.ImageTools;
import com.myDialog.mySpinnerItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Base64;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class SignUpActivity extends Activity {
	private String TAG = "SignUpActivity";
	private EditText name,around,last,lawman,id,idfront
					,idback,tax,serve,address,contactmen,
					tel,mail,mailnum,loginid,loginpass,confirmpass
					,paypass,confirmpaypass,idtime;
	private Spinner  agentType,IDType;
	private TextView name_error,around_error,last_error,lawman_error
					,id_error,tax_error,serve_error,contactmen_error
					,tel_error,mail_error,mailnum_error,loginpass_error
					,loginid_error,confirmpass_error,paypass_error,confirmpaypass_error;
	private RadioGroup radiogroup;
	private RadioButton nopass,withpass;
	private CheckBox checkBox1,channels1,channels2,channels3,channels4;
	private LinearLayout linpaypass,linconfirmpaypass;
	private Button save,close;
	private ImageView upfront,upback;
	
	private CustomProgressDialog progressdialog;
	DatePickerDialog mdatePicker;
    private String mRspCode = "";
    private String mRspMeg = "";
	private String picPath = null;
	private String picBasef = null;
	private String picBaseb = null;
	private String picBaseftype = null;
	private String picBasebtype = null;
	private boolean ISDEBUG = true;
	private static final int PHOTO_WITH_DATA_FRONT = 18;  //从SD卡中得到图片
	private static final int PHOTO_WITH_CAMERA_FRONT = 37;// 拍摄照片
	private static final int PHOTO_WITH_DATA_BACK = 19;  //从SD卡中得到图片
	private static final int PHOTO_WITH_CAMERA_BACK = 38;// 拍摄照片
	private String imgNameFront = "";
	private String imgNameBack = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SystemUtil.setAppLanguageChange(SignUpActivity.this);
		// 设置主题样式
        if (GlobalParams.Theme == 1) {
            setTheme(R.style.VioletTheme);
        } else if (GlobalParams.Theme == 2) {
            setTheme(R.style.OrangeTheme);
        }
		
		setContentView(R.layout.activity_signup);
		agentType = (Spinner) findViewById(R.id.agentType);
		List<mySpinnerItem> myitem = new ArrayList<mySpinnerItem>();
		myitem.add(new mySpinnerItem("1",getString(R.string.AGENT_TYPE_ITEM1)));
		myitem.add(new mySpinnerItem("2",getString(R.string.AGENT_TYPE_ITEM2)));
		ArrayAdapter<mySpinnerItem> adapter 
				= new ArrayAdapter<mySpinnerItem>(SignUpActivity.this,android.R.layout.simple_spinner_item,myitem);
		agentType.setAdapter(adapter);
		
		
		IDType = (Spinner) findViewById(R.id.IDType);
		List<mySpinnerItem> iditem = new ArrayList<mySpinnerItem>();
		iditem.add(new mySpinnerItem("0",getString(R.string.CRET_TYPE_ITEM1)));
		iditem.add(new mySpinnerItem("1",getString(R.string.CRET_TYPE_ITEM2)));
		iditem.add(new mySpinnerItem("2",getString(R.string.CRET_TYPE_ITEM3)));
		iditem.add(new mySpinnerItem("I",getString(R.string.CRET_TYPE_ITEM4)));
		iditem.add(new mySpinnerItem("C",getString(R.string.CRET_TYPE_ITEM5)));
		iditem.add(new mySpinnerItem("F",getString(R.string.CRET_TYPE_ITEM6)));
		iditem.add(new mySpinnerItem("G",getString(R.string.CRET_TYPE_ITEM7)));
		iditem.add(new mySpinnerItem("Z",getString(R.string.CRET_TYPE_ITEM8)));
		ArrayAdapter adapterid 
					= new ArrayAdapter<mySpinnerItem>(SignUpActivity.this, android.R.layout.simple_spinner_item, iditem);
		IDType.setAdapter(adapterid);
		
		
		name = (EditText) findViewById(R.id.name);
		name.setOnFocusChangeListener(focusChanged);
		around = (EditText) findViewById(R.id.around);
		around.setOnFocusChangeListener(focusChanged);
		last = (EditText) findViewById(R.id.last);
		last.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        InputFilter[] filters = {new CashierInpustFilter()};
        last.setFilters(filters);
		lawman = (EditText) findViewById(R.id.lawman);
		lawman.setOnFocusChangeListener(focusChanged);
		id = (EditText) findViewById(R.id.id);
		id.setOnFocusChangeListener(focusChanged);
		idfront = (EditText) findViewById(R.id.idfront);
//		idfront.setOnFocusChangeListener(focusChanged);
		upfront = (ImageView) findViewById(R.id.upfront);
		upfront.setOnClickListener(selectPicFront);
		idback = (EditText) findViewById(R.id.idback);
//		idback.setOnFocusChangeListener(focusChanged);
		upback = (ImageView) findViewById(R.id.upback);
		upback.setOnClickListener(selectPicBack);
		tax = (EditText) findViewById(R.id.tax);
		tax.setOnFocusChangeListener(focusChanged);
		serve = (EditText) findViewById(R.id.serve);
		serve.setOnFocusChangeListener(focusChanged);
		address = (EditText) findViewById(R.id.address);
		contactmen = (EditText) findViewById(R.id.contactmen);
		contactmen.setOnFocusChangeListener(focusChanged);
		tel = (EditText) findViewById(R.id.tel);
		tel.setOnFocusChangeListener(focusChanged);
		mail = (EditText) findViewById(R.id.mail);
		mail.setOnFocusChangeListener(focusChanged);
		mailnum = (EditText) findViewById(R.id.mailnum);
		mailnum.setOnFocusChangeListener(focusChanged);
		loginid = (EditText) findViewById(R.id.loginid);
		loginid.setOnFocusChangeListener(focusChanged);
		loginpass = (EditText) findViewById(R.id.loginpass);
		loginpass.setOnFocusChangeListener(focusChanged);
		confirmpass = (EditText) findViewById(R.id.confirmpass);
		confirmpass.setOnFocusChangeListener(focusChanged);
		
		
		paypass = (EditText) findViewById(R.id.paypass);
		paypass.setOnFocusChangeListener(focusChanged);
		confirmpaypass = (EditText) findViewById(R.id.confirmpaypass);
		confirmpaypass.setOnFocusChangeListener(focusChanged);
		radiogroup =  (RadioGroup) findViewById(R.id.radioGroup);
		radiogroup.setOnCheckedChangeListener(checkChanged);
		nopass =  (RadioButton) findViewById(R.id.nopass);
		withpass =  (RadioButton) findViewById(R.id.withpass);
		linpaypass = (LinearLayout) findViewById(R.id.linpaypass);
		linconfirmpaypass =(LinearLayout) findViewById(R.id.linconfirmpaypass);
		checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
		checkBox1.setOnCheckedChangeListener(fucheckChange);
		channels1 = (CheckBox) findViewById(R.id.channels1);
		channels2 = (CheckBox) findViewById(R.id.channels2);
		channels3 = (CheckBox) findViewById(R.id.channels3);
		channels4 = (CheckBox) findViewById(R.id.channels4);
		idtime = (EditText) findViewById(R.id.idtime);
		idtime.setOnFocusChangeListener(focusChanged);
		idtime.setOnClickListener(showmdatePicker);
		Calendar cal = Calendar.getInstance();  
		mdatePicker=new DatePickerDialog(SignUpActivity.this,null,
				cal.get(Calendar.YEAR),cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		
		mdatePicker.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.network_setting_server_address_button_ensure), new DialogInterface.OnClickListener() {  
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
                //通过mDialog.getDatePicker()获得dialog上的DatePicker组件，然后可以获取日期信息  
                DatePicker datePicker = mdatePicker.getDatePicker();  
                int year = datePicker.getYear();  
                int month = datePicker.getMonth()+1;
                String thismonth = ""+month;
                if(month<10){
                	thismonth = "0"+month;
                }
                int day = datePicker.getDayOfMonth(); 
                String thisday= ""+day;
                if(day<10){
                	thisday = "0"+day;
                }
                idtime.setText(year+"-"+thismonth+"-"+thisday);
            }  
        });  
		mdatePicker.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.network_setting_server_address_button_cancel), new DialogInterface.OnClickListener() {  
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
                dialog.cancel(); 
            }  
        });  
  
		
		
		
		save = (Button) findViewById(R.id.save);
		save.setOnClickListener(saveListen);
		close = (Button) findViewById(R.id.close);
		close.setOnClickListener(closeListen);
	}
	
	OnFocusChangeListener focusChanged = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			String temp;
			String mach = "";//mach = "^((13[0-9])|(15[^4,//D])|(18[0,5-9]))//d{8}$";
			if(!hasFocus){
				if(v == name){
					name_error = (TextView) findViewById(R.id.name_error);
					mach = "^[\\u4e00-\\u9fa5a-zA-Z0-9._]*$";
//					mach ="^[\\u0391-\\uFFE5\\a-zA-Z0-9\\u0391-\\uFFE5\\s*\\.\\。\\:\\']*$";
					temp = name.getText().toString().trim();
					if(temp.equals("") || !isMatched(temp,mach)){
						name_error.setVisibility(View.VISIBLE);
					}else{
						name_error.setVisibility(View.GONE);
					}
					name.setText(temp);
				}else if(v == around){
					around_error = (TextView) findViewById(R.id.around_error);
					mach = "^[\\u4e00-\\u9fa5a-zA-Z0-9._]*$";
					temp = around.getText().toString().trim();
					if(temp.equals("") || !isMatched(temp,mach)){
						around_error.setVisibility(View.VISIBLE);
					}else{
						around_error.setVisibility(View.GONE);
					}
					around.setText(temp);
				}else if(v == lawman){
					lawman_error = (TextView) findViewById(R.id.lawman_error);
					mach = "^[\\u4e00-\\u9fa5a-zA-Z0-9._]*$";
					temp = lawman.getText().toString().trim();
					if(temp.equals("") || !isMatched(temp,mach)){
						lawman_error.setVisibility(View.VISIBLE);
					}else{
						lawman_error.setVisibility(View.GONE);
					}
					lawman.setText(temp);
				}else if(v == id){
					id_error = (TextView) findViewById(R.id.id_error);
					mach = "[\\a-zA-Z0-9]*";
					temp = id.getText().toString().trim();
					if(temp.equals("") || !isMatched(temp,mach)){
						id_error.setVisibility(View.VISIBLE);
					}else{
						id_error.setVisibility(View.GONE);
					}
					id.setText(temp);
				}else if(v == tax){
					tax_error = (TextView) findViewById(R.id.tax_error);
					mach = "[\\w.]*";
					temp = tax.getText().toString().trim();
					if(temp.equals("") || !isMatched(temp,mach)){
						tax_error.setVisibility(View.VISIBLE);
					}else{
						tax_error.setVisibility(View.GONE);
					}
					tax.setText(temp);
				}else if(v == serve){
					serve_error = (TextView) findViewById(R.id.serve_error);
					mach = "[0-9_]*";
					temp = serve.getText().toString().trim();
					if(temp.equals("") || !isMatched(temp,mach)){
						serve_error.setVisibility(View.VISIBLE);
					}else{
						serve_error.setVisibility(View.GONE);
					}
					serve.setText(temp);
				}else if(v == contactmen){
					contactmen_error = (TextView) findViewById(R.id.contactmen_error);
					mach = "^[\\u4e00-\\u9fa5a-zA-Z0-9._]*$";
					temp = contactmen.getText().toString().trim();
					if(temp.equals("") || !isMatched(temp,mach)){
						contactmen_error.setVisibility(View.VISIBLE);
					}else{
						contactmen_error.setVisibility(View.GONE);
					}
					contactmen.setText(temp);
				}else if(v == tel){
					tel_error = (TextView) findViewById(R.id.tel_error);
					mach = "[0-9_]*";
					temp = tel.getText().toString().trim();
					if(temp.equals("") || !isMatched(temp,mach)){
						tel_error.setVisibility(View.VISIBLE);
					}else{
						tel_error.setVisibility(View.GONE);
					}
					tel.setText(temp);
				}else if(v == mail){
					mail_error = (TextView) findViewById(R.id.mail_error);
					mach = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}([;]\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14})*";
					temp = mail.getText().toString().trim();
					if(temp.equals("") || !isMatched(temp,mach)){
						mail_error.setVisibility(View.VISIBLE);
					}else{
						mail_error.setVisibility(View.GONE);
					}
					mail.setText(temp);
				}else if(v == mailnum){
					mailnum_error = (TextView) findViewById(R.id.mailnum_error);
					mach = "[0-9]*";
					temp = mailnum.getText().toString().trim();
					if(!temp.equals("") && !isMatched(temp,mach)){
						mailnum_error.setVisibility(View.VISIBLE);
					}else{
						mailnum_error.setVisibility(View.GONE);
					}
					mailnum.setText(temp);
				}else if(v == loginid){
					loginid_error = (TextView) findViewById(R.id.loginid_error);
					mach = "^\\w{6,16}$";
					temp = loginid.getText().toString().trim();
					if(temp.equals("") || !isMatched(temp,mach)){
						loginid_error.setVisibility(View.VISIBLE);
					}else{
						loginid_error.setVisibility(View.GONE);
					}
					loginid.setText(temp);
				}else if(v == loginpass){
					loginpass_error = (TextView) findViewById(R.id.loginpass_error);
					mach = "^(?!^(\\d+|[a-zA-Z]+|[~!@#$%^&*?]+)$)^[\\w~!@#$%\\^&*?]+$";
					temp = loginpass.getText().toString().trim();
					if(temp.equals("") || !isMatched(temp,mach)){
						loginpass_error.setVisibility(View.VISIBLE);
					}else{
						loginpass_error.setVisibility(View.GONE);
					}
					loginpass.setText(temp);
				}else if(v == confirmpass){
					confirmpass_error = (TextView) findViewById(R.id.confirmpass_error);
					temp = confirmpass.getText().toString().trim();
					if(temp.equals("") || !(temp.equals(loginpass.getText().toString().trim()))){
						confirmpass_error.setVisibility(View.VISIBLE);
					}else{
						confirmpass_error.setVisibility(View.GONE);
					}
					confirmpass.setText(temp);
				}else if(v == paypass){
					int radioid = radiogroup.getCheckedRadioButtonId();
					if(radioid == R.id.withpass){
						paypass_error = (TextView) findViewById(R.id.paypass_error);
						mach = "(?!^(\\d+|[a-zA-Z]+|[~!@#$%^&*?]+)$)^[\\w~!@#$%\\^&*?]+$";
						temp = paypass.getText().toString().trim();
						if(temp.equals("") || !isMatched(temp,mach)){
							paypass_error.setVisibility(View.VISIBLE);
						}else{
							paypass_error.setVisibility(View.GONE);
						}
						paypass.setText(temp);
					}
				}else if(v == confirmpaypass){
					int radioid = radiogroup.getCheckedRadioButtonId();
					if(radioid == R.id.withpass){
						confirmpaypass_error = (TextView) findViewById(R.id.confirmpaypass_error);
						mach = "(?!^(\\d+|[a-zA-Z]+|[~!@#$%^&*?]+)$)^[\\w~!@#$%\\^&*?]+$";
						temp = confirmpaypass.getText().toString().trim();
						if(temp.equals("") || !(temp.equals(paypass.getText().toString().trim()))){
							confirmpaypass_error.setVisibility(View.VISIBLE);
						}else{
							confirmpaypass_error.setVisibility(View.GONE);
						}
						confirmpaypass.setText(temp);
					}
				}else if(v == idtime){
					Calendar c = Calendar.getInstance();  
					int year = c.get(Calendar.YEAR);  
					int month = c.get(Calendar.MONTH)+1;  
					int day = c.get(Calendar.DAY_OF_MONTH); 
					String thismonth = ""+month;
	                if(month<10){
	                	thismonth = "0"+month;
	                }
	                String thisday= ""+day;
	                if(day<10){
	                	thisday = "0"+day;
	                }
					String dateToday = year+"-"+thismonth+"-"+thisday;
					if(idtime.getText().toString().equals("")){
						SystemUtil.displayToast(SignUpActivity.this, getString(R.string.msg_error_timeblank));
					}else if(idtime.getText().toString().compareTo(dateToday) < 0){
						SystemUtil.displayToast(SignUpActivity.this, getString(R.string.msg_error_timerror));
						idtime.setText("");
					}
				}
				
			}else{
					/*if(v == idfront){
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						//回调图片类使用的
						startActivityForResult(intent, RESULT_CANCELED);
					}else if(v == idback){
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						//回调图片类使用的
						startActivityForResult(intent, RESULT_FIRST_USER);
					}*/
			}
		}
	};
	
	/**
	 * 回调执行的方法
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==Activity.RESULT_OK)
		{
			if(requestCode == PHOTO_WITH_DATA_FRONT || requestCode == PHOTO_WITH_DATA_BACK){
				/**
				 * 当选择的图片不为空的话，在获取到图片的途径  
				 */
				Uri uri = data.getData();
				Log.e(TAG, "uri = "+ uri);
				try {
					String[] pojo = {MediaStore.Images.Media.DATA};
					
					Cursor cursor = managedQuery(uri, pojo, null, null,null);
					if(cursor!=null)
					{
						ContentResolver cr = this.getContentResolver();
						int colunm_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						cursor.moveToFirst();
						String path = cursor.getString(colunm_index);
						/***
						 * 这里加这样一个判断主要是为了第三方的软件选择，比如：使用第三方的文件管理器的话，你选择的文件就不一定是图片了，这样的话，我们判断文件的后缀名
						 * 如果是图片格式的话，那么才可以   
						 */
						if(path.endsWith("jpg")||path.endsWith("png"))
						{
							picPath = path;
	//						BitmapFactory.Options options = new BitmapFactory.Options();
	//						options.inJustDecodeBounds = true;
	//						Bitmap bitmaptemp = BitmapFactory.decodeFile(path, options);/* 这里返回的bmp是null */
	//						Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
							
							InputStream input = SignUpActivity.this.getContentResolver().openInputStream(uri);
					        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
					        onlyBoundsOptions.inJustDecodeBounds = true;
					        onlyBoundsOptions.inDither = true;//optional
					        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
					        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
					        input.close();
					        int originalWidth = onlyBoundsOptions.outWidth;
					        int originalHeight = onlyBoundsOptions.outHeight;
					        if ((originalWidth == -1) || (originalHeight == -1))
					        	alert();
							 //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
					        int be = 1;//be=1表示不缩放
					        //图片分辨率以480x800为标准
					        int hh = 800;//这里设置高度为800f
					        int ww = 480;//这里设置宽度为480f
					        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
					            be = (int) (originalWidth / ww);
					        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
					            be = (int) (originalHeight / hh);
					        }
						   if(be<1)
							   be = 1;
						   
							BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
							bitmapOptions.inSampleSize = be;//缩放比例
							bitmapOptions.inDither  = true;
							bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
							input = SignUpActivity.this.getContentResolver().openInputStream(uri);
							Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
							input.close();
							bitmap = compressImage(bitmap);
							
							if(requestCode == PHOTO_WITH_DATA_FRONT){
								picBasef = imgToBase64(bitmap);
								if(path.endsWith("jpg")){
									picBaseftype = ".jpg";
								}else{
									picBaseftype = ".png";
								}
								upfront.setImageBitmap(bitmap);
										
							}else{
								picBaseb = imgToBase64(bitmap);
								if(path.endsWith("jpg")){
									picBasebtype = ".jpg";
								}else{
									picBasebtype = ".png";
								}
								upback.setImageBitmap(bitmap);
							}
							
						}else{
							alert();
						}
					}else{
						alert();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				String status = Environment.getExternalStorageState();
				String path = Environment.getExternalStorageDirectory()+"/image.jpg";
				File file = new File(path);
				Uri uri = Uri.fromFile(file);
				try{
						InputStream input = SignUpActivity.this.getContentResolver().openInputStream(uri);
				        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
				        onlyBoundsOptions.inJustDecodeBounds = true;
				        onlyBoundsOptions.inDither = true;//optional
				        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
				        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
				        input.close();
				        int originalWidth = onlyBoundsOptions.outWidth;
				        int originalHeight = onlyBoundsOptions.outHeight;
				        if ((originalWidth == -1) || (originalHeight == -1))
				        	alert();
					 //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
			        int be = 1;//be=1表示不缩放
			        //图片分辨率以480x800为标准
			        int hh = 800;//这里设置高度为800f
			        int ww = 480;//这里设置宽度为480f
			        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
			            be = (int) (originalWidth / ww);
			        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
			            be = (int) (originalHeight / hh);
			        }
				   if(be<1)
					   be = 1;
				   
					BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
					bitmapOptions.inSampleSize = be;//缩放比例
					bitmapOptions.inDither  = true;
					bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
					input = SignUpActivity.this.getContentResolver().openInputStream(uri);
					Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
					input.close();
					bitmap = compressImage(bitmap);
					
	//					Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/image.jpg");
						
						if(requestCode == PHOTO_WITH_CAMERA_FRONT){
							picBasef = imgToBase64(bitmap);
							picBaseftype = ".jpg";
							upfront.setImageBitmap(bitmap);
						}else{
							picBaseb = imgToBase64(bitmap);
							picBasebtype = ".jpg";
							upback.setImageBitmap(bitmap);
						}
					
//					imgNameFront = createPhotoFileName();
					//写一个方法将此文件保存到本应用下面啦
//	            	savePicture(imgNameFront,bitmap);
//	            	if (bitmap != null) {
//						//为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
//						Bitmap smallBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / 5, bitmap.getHeight() / 5);
//						if(requestCode == PHOTO_WITH_CAMERA_FRONT){
//							upfront.setImageBitmap(smallBitmap);
//						}else{
//							upback.setImageBitmap(smallBitmap);
//						}
//					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**创建图片不同的文件名**/
	private String createPhotoFileName() {
		String fileName = "";
		Date date = new Date(System.currentTimeMillis());  //系统当前时间
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
		fileName = dateFormat.format(date) + ".jpg";
		return fileName;
	}
	
	/**
	    * 质量压缩方法
	    *
	    * @param image
	    * @return
	    */
	   public static Bitmap compressImage(Bitmap image) {
	 
	       ByteArrayOutputStream baos = new ByteArrayOutputStream();
	       image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
	       int options = 100;
	       while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
	           baos.reset();//重置baos即清空baos
	           //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
	           image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
	           options -= 10;//每次都减少10
	       }
	       ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
	       Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
	       return bitmap;
	   }
    /** 
     *  
     * @param imgPath 
     * @param bitmap 
     * @param imgFormat 图片格式 
     * @return 
     */  
    public static String imgToBase64(Bitmap bitmap) {  
        if(bitmap == null){  
            //bitmap not found!!  
        }  
        ByteArrayOutputStream out = null;  
        try {  
            out = new ByteArrayOutputStream();  
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);  
  
            out.flush();  
            out.close();  
  
            byte[] imgBytes = out.toByteArray();  
            return Base64.encodeToString(imgBytes, Base64.DEFAULT);  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            return null;  
        } finally {  
            try {  
                out.flush();  
                out.close();  
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
        }  
    }  
  
    private static Bitmap readBitmap(String imgPath) {  
        try {  
            return BitmapFactory.decodeFile(imgPath);  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            return null;  
        }  
  
    }
    
    OnClickListener selectPicFront = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			/*Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			//回调图片类使用的
			startActivityForResult(intent, RESULT_CANCELED);*/
			openPictureSelectDialog(PHOTO_WITH_DATA_FRONT);
		}
	};
	 OnClickListener selectPicBack = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				//回调图片类使用的
				startActivityForResult(intent, RESULT_FIRST_USER);*/
				openPictureSelectDialog(PHOTO_WITH_DATA_BACK);
			}
		};
		
		/**打开对话框**/
		private void openPictureSelectDialog(int reqCode) {
			//自定义Context,添加主题
			final int reqcode = reqCode;
			Context dialogContext = new ContextThemeWrapper(SignUpActivity.this, android.R.style.Theme_Light);
			String[] choiceItems= new String[2];
			choiceItems[0] = getString(R.string.sign_pic_photo);  //拍照
			choiceItems[1] = getString(R.string.sign_pic_picture);  //从相册中选择
			ListAdapter adapter = new ArrayAdapter<String>(dialogContext, android.R.layout.simple_list_item_1,choiceItems);
			//对话框建立在刚才定义好的上下文上
			AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
			builder.setTitle(R.string.sign_pic);
			builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:  //相机
						if(reqcode == PHOTO_WITH_DATA_FRONT){
							doTakePhoto(PHOTO_WITH_CAMERA_FRONT);
						}else if(reqcode == PHOTO_WITH_DATA_BACK){
							doTakePhoto(PHOTO_WITH_CAMERA_BACK);
						}
						break;
					case 1:  //从图库相册中选取
						if(reqcode == PHOTO_WITH_DATA_FRONT){
							doPickPhotoFromGallery(PHOTO_WITH_DATA_FRONT);
						}else if(reqcode == PHOTO_WITH_DATA_BACK){
							doPickPhotoFromGallery(PHOTO_WITH_DATA_BACK);
						}
						break;
					}
					dialog.dismiss();
				}
			});
			builder.create().show();
		}	
		
		/**从相册获取图片**/
		private void doPickPhotoFromGallery(int reqCode) {
			/*Intent intent = new Intent();
			intent.setType("image/*");  // 开启Pictures画面Type设定为image
			intent.setAction(Intent.ACTION_GET_CONTENT); //使用Intent.ACTION_GET_CONTENT这个Action 
			startActivityForResult(intent, reqCode); //取得相片后返回到本画面
*/		
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			//回调图片类使用的
			startActivityForResult(intent, reqCode);
			
		}
		 
		/**拍照获取相片**/
		private void doTakePhoto(int reqCode) {
		    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //调用系统相机
		   
		    Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"image.jpg"));
			//指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
		    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
	       
		    //直接使用，没有缩小  
	        startActivityForResult(intent, reqCode);  //用户点击了从相机获取
		}
	
	private void alert()
    {
    	Dialog dialog = new AlertDialog.Builder(this)
		.setTitle(R.string.progress_tishi_title)
		.setMessage(R.string.sign_pic_wp)
		.setPositiveButton(R.string.dialog_comfirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						picPath = null;
					}
				})
		.create();
		dialog.show();
    }

	
	OnClickListener showmdatePicker = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(mdatePicker.isShowing()){
				mdatePicker.hide();
			}else{
				mdatePicker.show();
			}
		}
	};

	public static boolean isMatched(String mobiles,String mach) {  
		if(mach.equals("") || mach == null){
			return true;
		}
        Pattern p = Pattern   
                .compile(mach);   
        Matcher m = p.matcher(mobiles);   
        return m.matches();   
    }   
	
//	是否需要支付密码
	OnCheckedChangeListener checkChanged = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			int radioid = group.getCheckedRadioButtonId();
			if(radioid == R.id.nopass){
				linpaypass.setVisibility(View.GONE);
				linconfirmpaypass.setVisibility(View.GONE);
				paypass.setText("");
				confirmpaypass.setText("");
			}else{
				linpaypass.setVisibility(View.VISIBLE);
				linconfirmpaypass.setVisibility(View.VISIBLE);
			}
		}
		
	};
//	是否余额提醒
	android.widget.CompoundButton.OnCheckedChangeListener fucheckChange = new CompoundButton.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if(isChecked){
				last.setVisibility(View.VISIBLE);
			}else{
				last.setVisibility(View.GONE);
			}
		}
	};
	
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case 0:
				 try {
                     if (progressdialog != null&& progressdialog.isShowing()) {
                    	 progressdialog.dismiss();
                     }
                     // 没有加载到数据，页码返回到当前页
                     Toast.makeText(SignUpActivity.this, getString(R.string.str_lianwangshibai), Toast.LENGTH_LONG).show();
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
				//联网失败
				break;
			case 1:
				  if (progressdialog != null&& progressdialog.isShowing()) {
                 	 progressdialog.dismiss();
                  }
				  String rspCode = Client.Parse_XML(GlobalParams.RETURN_DATA,
                          "<RSPCOD>", "</RSPCOD>");
                  mRspMeg = Client.Parse_XML(GlobalParams.RETURN_DATA,
                          "<RSPMSG>", "</RSPMSG>");
                  if (!rspCode.equals("00000")) {// 请求失败
                      // 服务器返回系统超时，返回到登录页面
                      if (rspCode.equals("00011")) {
                          Toast.makeText(SignUpActivity.this, mRspMeg,
                                  Toast.LENGTH_LONG).show();
                          SystemUtil.setGlobalParamsToNull(SignUpActivity.this);
                          DummyContent.ITEM_MAP.clear();
                          DummyContent.ITEMS.clear();
                          Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                          SignUpActivity.this.startActivity(intent);
                      } else {
                          if (!mRspMeg.equalsIgnoreCase("")) {
                              SystemUtil.displayToast(
                            		  SignUpActivity.this, mRspMeg);
                          } else {
                              SystemUtil
                                      .displayToast(
                                    		  SignUpActivity.this,
                                              getString(R.string.sign_failed));
                          }
                      }
                  } else {
                      if (!mRspMeg.equalsIgnoreCase("")) {
                          SystemUtil.displayToast(
                        		  SignUpActivity.this, mRspMeg);
                      } else {
                          SystemUtil
                                  .displayToast(
                                		  SignUpActivity.this,
                                          getString(R.string.sign_success));
                      }
                      save.setVisibility(View.GONE);
                      SystemClock.sleep(500);
                  }
                  break;
			}
			
			
			
			super.handleMessage(msg);
		}
	};
	
	OnClickListener saveListen = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			progressdialog = CustomProgressDialog.createProgressDialog(SignUpActivity.this, GlobalParams.PROGRESSDIALOG_TIMEOUT,
	                new CustomProgressDialog.OnTimeOutListener() {

	                    @Override
	                    public void onTimeOut(CustomProgressDialog dialog) {
	                        Toast.makeText(SignUpActivity.this,
	                                getString(R.string.progress_timeout),
	                                Toast.LENGTH_LONG).show();
	                        if (dialog != null
	                                && (!SignUpActivity.this.isFinishing())) {
	                            dialog.dismiss();
	                            dialog = null;
	                        }

	                    }
	                }
	        );
			progressdialog.setTitle(R.string.progress_tishi_title);
			progressdialog.setMessage(getString(R.string.sign_loading));
//			重新校验
			String temp;
			String mach = "";
				name_error = (TextView) findViewById(R.id.name_error);
				mach = "^[\\u4e00-\\u9fa5a-zA-Z0-9._]*$";
				temp = name.getText().toString().trim();
				if(temp.equals("") || !isMatched(temp,mach)){
					name_error.setVisibility(View.VISIBLE);
					name.requestFocus();
					InputMethodManager inputManager =(InputMethodManager)name.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.showSoftInput(name, 0);
							
					return;
				}else{
					name_error.setVisibility(View.GONE);
				}
				name.setText(temp);
				around_error = (TextView) findViewById(R.id.around_error);
				mach = "^[\\u4e00-\\u9fa5a-zA-Z0-9._]*$";
				temp = around.getText().toString().trim();
				if(temp.equals("") || !isMatched(temp,mach)){
					around_error.setVisibility(View.VISIBLE);
					around.requestFocus();
					return;
				}else{
					around_error.setVisibility(View.GONE);
				}
				around.setText(temp);
				lawman_error = (TextView) findViewById(R.id.lawman_error);
				mach = "^[\\u4e00-\\u9fa5a-zA-Z0-9._]*$";
				temp = lawman.getText().toString().trim();
				if(temp.equals("") || !isMatched(temp,mach)){
					lawman_error.setVisibility(View.VISIBLE);
					lawman.requestFocus();
					return;
				}else{
					lawman_error.setVisibility(View.GONE);
				}
				lawman.setText(temp);
				id_error = (TextView) findViewById(R.id.id_error);
				mach = "[\\a-zA-Z0-9]*";
				temp = id.getText().toString().trim();
				if(temp.equals("") || !isMatched(temp,mach)){
					id_error.setVisibility(View.VISIBLE);
					id.requestFocus();
					return;
				}else{
					id_error.setVisibility(View.GONE);
				}
				id.setText(temp);
				tax_error = (TextView) findViewById(R.id.tax_error);
				mach = "[\\w.]*";
				temp = tax.getText().toString().trim();
				if(temp.equals("") || !isMatched(temp,mach)){
					tax_error.setVisibility(View.VISIBLE);
					tax.requestFocus();
					return;
				}else{
					tax_error.setVisibility(View.GONE);
				}
				tax.setText(temp);
				serve_error = (TextView) findViewById(R.id.serve_error);
				mach = "[0-9_]*";
				temp = serve.getText().toString().trim();
				if(temp.equals("") || !isMatched(temp,mach)){
					serve_error.setVisibility(View.VISIBLE);
					serve.requestFocus();
					return;
				}else{
					serve_error.setVisibility(View.GONE);
				}
				serve.setText(temp);
				contactmen_error = (TextView) findViewById(R.id.contactmen_error);
				mach = "^[\\u4e00-\\u9fa5a-zA-Z0-9._]*$";
				temp = contactmen.getText().toString().trim();
				if(temp.equals("") || !isMatched(temp,mach)){
					contactmen_error.setVisibility(View.VISIBLE);
					contactmen.requestFocus();
					return;
				}else{
					contactmen_error.setVisibility(View.GONE);
				}
				contactmen.setText(temp);
				tel_error = (TextView) findViewById(R.id.tel_error);
				mach = "[0-9_]*";
				temp = tel.getText().toString().trim();
				if(temp.equals("") || !isMatched(temp,mach)){
					tel_error.setVisibility(View.VISIBLE);
					tel.requestFocus();
					return;
				}else{
					tel_error.setVisibility(View.GONE);
				}
				tel.setText(temp);
				mail_error = (TextView) findViewById(R.id.mail_error);
				mach = "^\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}([;]\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14})*$";
				temp = mail.getText().toString().trim();
				if(temp.equals("") || !isMatched(temp,mach)){
					mail_error.setVisibility(View.VISIBLE);
					mail.requestFocus();
					return;
				}else{
					mail_error.setVisibility(View.GONE);
				}
				mail.setText(temp);
				mailnum_error = (TextView) findViewById(R.id.mailnum_error);
				mach = "[0-9]*";
				temp = mailnum.getText().toString().trim();
				if(!temp.equals("") && !isMatched(temp,mach)){
					mailnum_error.setVisibility(View.VISIBLE);
					mailnum.requestFocus();
					return;
				}else{
					mailnum_error.setVisibility(View.GONE);
				}
				mailnum.setText(temp);
				loginid_error = (TextView) findViewById(R.id.loginid_error);
				mach = "^\\w{6,16}$";
				temp = loginid.getText().toString().trim();
				if(temp.equals("") || !isMatched(temp,mach)){
					loginid_error.setVisibility(View.VISIBLE);
					loginid.requestFocus();
					return;
				}else{
					loginid_error.setVisibility(View.GONE);
				}
				loginid.setText(temp);
				loginpass_error = (TextView) findViewById(R.id.loginpass_error);
				mach = "(?!^(\\d+|[a-zA-Z]+|[~!@#$%^&*?]+)$)^[\\w~!@#$%\\^&*?]+$";
				temp = loginpass.getText().toString().trim();
				if(temp.equals("") || !isMatched(temp,mach) ){
					loginpass_error.setVisibility(View.VISIBLE);
					loginpass.requestFocus();
					return;
				}else{
					if(!(temp.equals(loginpass.getText().toString().trim()))){
						confirmpass_error.setVisibility(View.VISIBLE);
						confirmpass.requestFocus();
						return;
					}
					loginpass_error.setVisibility(View.GONE);
				}
				loginpass.setText(temp);
				confirmpass_error = (TextView) findViewById(R.id.confirmpass_error);
				temp = confirmpass.getText().toString().trim();
				if(temp.equals("") || !(temp.equals(loginpass.getText().toString().trim()))){
					confirmpass_error.setVisibility(View.VISIBLE);
					confirmpass.requestFocus();
					return;
				}else{
					confirmpass_error.setVisibility(View.GONE);
				}
				confirmpass.setText(temp);
				int radioid = radiogroup.getCheckedRadioButtonId();
				if(radioid == R.id.withpass){
						paypass_error = (TextView) findViewById(R.id.paypass_error);
						mach = "(?!^(\\d+|[a-zA-Z]+|[~!@#$%^&*?]+)$)^[\\w~!@#$%\\^&*?]+$";
						temp = paypass.getText().toString().trim();
						if(temp.equals("") || !isMatched(temp,mach)){
							paypass_error.setVisibility(View.VISIBLE);
							paypass.requestFocus();
							return;
						}else{
							if(!(temp.equals(paypass.getText().toString().trim()))){
								confirmpaypass_error.setVisibility(View.VISIBLE);
								confirmpaypass.requestFocus();
								return;
							}
							paypass_error.setVisibility(View.GONE);
						}
						loginpass.setText(temp);
						confirmpaypass_error = (TextView) findViewById(R.id.confirmpaypass_error);
						mach = "(?!^(\\d+|[a-zA-Z]+|[~!@#$%^&*?]+)$)^[\\w~!@#$%\\^&*?]+$";
						temp = confirmpaypass.getText().toString().trim();
						if(temp.equals("") || !isMatched(temp,mach)){
							confirmpaypass_error.setVisibility(View.VISIBLE);
							confirmpaypass.requestFocus();
							return;
						}else{
							confirmpaypass_error.setVisibility(View.GONE);
						}
						confirmpaypass.setText(temp);
				}
				
//				发起请求
				
				 Request_Signup_Confirm.setContext(SignUpActivity.this);
				 Request_Signup_Confirm.setSTORE_NAME(name.getText().toString());
				 Request_Signup_Confirm.setAGENT_TYPE(((mySpinnerItem) agentType.getSelectedItem()).getID());
				 Request_Signup_Confirm.setBUSINESS_SCOPE(around.getText().toString());
				 if(checkBox1.isChecked()){
					 Request_Signup_Confirm.setBALANCE_REM("1");
					 Request_Signup_Confirm.setREM_AMT(last.getText().toString());
				 }else{
					 Request_Signup_Confirm.setBALANCE_REM("0");
				 }
				 Request_Signup_Confirm.setLAW_NAME(lawman.getText().toString());
				 if(picBasef != null){
					 Request_Signup_Confirm.setCRET_FRONT_PICTURE(picBasef);
					 Request_Signup_Confirm.setCRET_FRONT_PICTURE_TYPE(picBaseftype);
				 }
				 if(picBaseb != null){
					 Request_Signup_Confirm.setCRET_BACK_PICTURE(picBaseb);
					 Request_Signup_Confirm.setCRET_BACK_PICTURE_TYPE(picBasebtype);
				 }
				 String channels = "";
				 if(channels1.isChecked()){
					 channels += ",1";
				 }
				 if(channels2.isChecked()){
					 channels += ",2";
				 }
				 if(channels3.isChecked()){
					 channels += ",3";
				 }
				 if(channels4.isChecked()){
					 channels += ",4";
				 }
				 if(!channels.equals("")){
					 channels = channels.substring(1);
				 }
				 Request_Signup_Confirm.setBUSINESS_CHANNELS(channels);
				 Request_Signup_Confirm.setLAW_PERSON_CRET_TYPE(((mySpinnerItem) IDType.getSelectedItem()).getID());
				 Request_Signup_Confirm.setLAW_PERSON_CRET_NO(id.getText().toString());
				 Request_Signup_Confirm.setCERT_DATE_END(idtime.getText().toString());
				 
				 if(nopass.isChecked()){
					 Request_Signup_Confirm.setBUY_ELE_WAY("1");
					 Request_Signup_Confirm.setPAY_PASSWD("");
				 }else{
					 Request_Signup_Confirm.setBUY_ELE_WAY("2");
					 Request_Signup_Confirm.setPAY_PASSWD(paypass.getText().toString());
				 }
				 
				 Request_Signup_Confirm.setTAX_REGISTRATION_NO(tax.getText().toString());
				 Request_Signup_Confirm.setSERVICE_TEL(serve.getText().toString());
				 Request_Signup_Confirm.setSUB_ADDRESS(address.getText().toString());
				 Request_Signup_Confirm.setCONTACTS_NAME(contactmen.getText().toString());
				 Request_Signup_Confirm.setCONTACTS_PHONE(tel.getText().toString());
				 Request_Signup_Confirm.setCONTACTS_EMAIL(mail.getText().toString());
				 Request_Signup_Confirm.setPOST_CODE(mailnum.getText().toString());
				 Request_Signup_Confirm.setLOGIN_USER_ID(loginid.getText().toString());
				 Request_Signup_Confirm.setLOGIN_USER_PASSWD(loginpass.getText().toString());
	                String APIName = "PAgentOpenAccSub";
	                String data = Request_Signup_Confirm.getRequsetXML();
	                Client.SendData(APIName, data, handler);
	                progressdialog.show();
		}
	};
	
	OnClickListener closeListen = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
	};
	
	
	
	
	  private void logMsg1(String msg) {
	    	if(ISDEBUG){
		    	//AlertDialog.Builder builder;
		    	AlertDialog.Builder  builder = new AlertDialog.Builder (this);
		   	  builder.setMessage(msg);
		   	  builder.setTitle("提示");
		   	 builder.setPositiveButton("OK",
		             new DialogInterface.OnClickListener() {
		   	   @Override
		   	   public void onClick(DialogInterface dialog, int which) {
		   		   	dialog.dismiss();
		   	   }
		   	  });
		   	  AlertDialog x = builder.create();
		   	  x.show();
	    	}
	  }
	
}
