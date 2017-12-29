package com.example.imitatesdk;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcB;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.eps.wlt2bmpdemo.DecodeUtil;
import com.mobile.android.idcard.sdk.IdCardApplication;
import com.mobile.android.idcard.sdk.R;
import com.mobile.android.idcard.sdk.handler.IHandler;
import com.mobile.android.idcard.sdk.handler.NfcBHandler;
import com.mobile.android.idcard.sdk.idcard.IdCardConstant;
import com.mobile.android.idcard.sdk.idcard.IdCardContext;
import com.mobile.android.idcard.sdk.idcard.IdCardInfo;
import com.zqd.idcard.CardManager;
import com.zqd.idcard.Util;
import com.zqd.idcard.ZqdReadIdCard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class CardInfoActivity extends Activity implements OnClickListener {

	private static EditText ed02;
	// 关于NFC
	public NfcAdapter nfcAdapter;
	private PendingIntent mPendingIntent;
	private boolean isFirsRun = true;
	private boolean isProcessIntent;
	public static NfcB isoDep;
	private int sysVersion;
	
	public IdCardApplication app = IdCardApplication.getInstance();
	
	byte[] manfile = null;
	Activity cardinfo = null;
	ProgressBar pb = null;
	private static ImageView imageView10066;
	private final static String encoding = "unicode";
	private static String[] showinfo= {	"姓名:" ,"性别:","民族:",	"出生日期:","住址:","身份证号:","签发机关:","起始日期:","失效日期:"};
	Button biaoti;
	View linearView;
	int time=0;
	public static final boolean debug = false;
	Bitmap face;
//	Tag tag;
	
	ZqdReadIdCard zqdreadId;

	private IdCardContext mIdCardContext;
	private String mContextKey;
	private IdCardInfo mIdCardInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_cardinfo);
		cardinfo = this;
		findView();
		initData();

		jiemi(getApplicationContext().getFilesDir());
		bmpPath =  getApplicationContext().getFilesDir() + "/wltlib/zp.bmp";
		wltPath =  getApplicationContext().getFilesDir() + "/wltlib/zp.wlt";

		//bmpPath =  Environment.getExternalStorageDirectory()+ "/wltlib/zp.bmp";
		//wltPath =  Environment.getExternalStorageDirectory() + "/wltlib/zp.wlt";

		parseExtras();
		zqdreadId = new ZqdReadIdCard(getApplicationContext(), myHandler);
		
		//区分系统版本
		sysVersion = Integer.parseInt(VERSION.SDK);
		if(sysVersion<19)
		 onNewIntent(getIntent());

		//pos机入口
		posReadCard();
	}

	private void posReadCard() {
		if (mIdCardContext.getDeviceHandler() != null) {
			zqdreadId.ReadBCard(host, port, 5000, mIdCardContext.getDeviceHandler());
		}
	}

	@Override
	public void onBackPressed() {
		mIdCardContext.setIdCardInfo(mIdCardInfo);
		mIdCardContext.sendIdCard();
		super.onBackPressed();
	}

	private void parseExtras() {
		Bundle bundle = getIntent().getExtras();
		if(bundle == null) {
			finish();
			return;
		}

		mContextKey = bundle.getString(IdCardConstant.KEY_CONTEXT_INDEX);
		mIdCardContext = IdCardContext.get(mContextKey);
	}

	private void jiemi(File tep){
		///File tep = 	getApplicationContext().getFilesDir();
		//File tep = Environment.getExternalStorageDirectory();
		String root = tep.toString();
		String rootFile =root+"/wltlib";

		File create = new File(rootFile);
		if(create.exists()){

		}else{
			create.mkdir();
		}
	}

	private void findView() {
		linearView = (LinearLayout)findViewById(R.id.linearView);
		ed02 = (EditText) findViewById(R.id.editText10082);
		pb = (ProgressBar) findViewById(R.id.progressBar1);
		imageView10066 = (ImageView)findViewById(R.id.imageView10066);
		biaoti = (Button)findViewById(R.id.biaoti);
		
	}
	private void initData() {
		cardinfo = this;
		app = (IdCardApplication) getApplication();
		app.addAcList(this);
		// 获取默认的NFC控制器
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (nfcAdapter == null) {
			return;
		}
		if (!nfcAdapter.isEnabled()) {
			return;
		}
		pb.setMax(100);
		pb.setProgress(0);
		face= BitmapFactory.decodeResource(getResources(), R.drawable.face);
	}

	@Override
	protected void onResume() {
		super.onResume();
		try{
		setForground();
		}catch(Exception e){
			Toast.makeText(cardinfo, "非NFC手机", Toast.LENGTH_LONG).show();
		}
		if (!isProcessIntent) {
			if(debug)if(debug) Log.e("hjs", "onresune");
			//processIntentB();
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		isProcessIntent = true;
		app.tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		if(debug) Log.e("hjs", "onNewIntent");
		ed02.setText("");
		if(face!=null)imageView10066.setImageBitmap(face);	
		//processIntentB();
		zqdreadId.NFCWithIntent(intent);
		ReadIDStard = System.currentTimeMillis();
		zqdreadId.ReadBCard(host, port, 5000);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (nfcAdapter != null) {
			disableNdefExchangeMode();
		}
		isProcessIntent = true;
		
	}

	// private static boolean flg = true;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// case R.id.rechargeBtn://我要充值
		// Intent intent = new Intent(this,RechargeActivity.class);
		// startActivity(intent);
		// flg =true;
		// break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// if(keyCode == KeyEvent.KEYCODE_BACK){
		// if(sliding.isOpened()){
		// sliding.animateClose();
		// return false;
		// }
		// }
		return super.onKeyDown(keyCode, event);
	}
	
	/////////////stard////////
	Long ReadIDStard = (long) 0;
	
	Long TotalEnd = (long) 0;
	private static final boolean Test =true;
	/////////////end/////////
    //--------------远程网关-----------
	String host = "123.57.221.61";
	//String host = "192.168.0.200";
	int port = 2202;
   //-----------------------------
	//int port = 5002;
//	String host = "20.20.1.180";
//	int port = 5002;


    //---------本地网关---
//    String host = "10.235.110.213";
//    int port = 7002;

	/**
	 * 设定前台系统可用
	 * 
	 * @param mPendingIntent
	 */
	private void enableNdefExchangeMode(PendingIntent mPendingIntent) {
		nfcAdapter.enableForegroundDispatch(this, mPendingIntent,
				CardManager.intentFiltersArray, CardManager.mTechLists);
	}

	/**
	 * 设定前台系统
	 * 
	 * @param mPendingIntent
	 */
	private void setForground() {
		// 设置优先级-前台发布系统，
		if (isFirsRun) {
			mPendingIntent = PendingIntent.getActivity(this, 0, getIntent(), 0);
			enableNdefExchangeMode(mPendingIntent);
			isFirsRun = false;
			enableReaderMode();
		}
	}

	  @TargetApi(19)
	    private void enableReaderMode() {
	    	if(sysVersion<19)
	    		return;
	    	Bundle options = new Bundle();
	    	options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 1000);
	        int READER_FLAGS =  NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
	        if (nfcAdapter != null) {
	        	nfcAdapter.enableReaderMode(this, new MyReaderCallback(), READER_FLAGS, options);
	        }
	    }

	  @TargetApi(19)
		public class MyReaderCallback implements NfcAdapter.ReaderCallback {

			@Override
			public void onTagDiscovered(final Tag arg0) {
				CardInfoActivity.this.runOnUiThread(new Runnable() {
		            @Override
		            public void run() {
		            app.tag =arg0;
		            //processIntentB();
		            zqdreadId.NFCWithTag(app.tag);
		            ReadIDStard = System.currentTimeMillis();

		    		zqdreadId.ReadBCard(host, port, 5000, null);
		            }
		        });
			}
		}
	  
	  
	/**
	 * 取消前台系统
	 */
	private void disableNdefExchangeMode() {
		nfcAdapter.disableForegroundDispatch(this);
		isFirsRun = true;
		disableReaderMode();
	}
	  @TargetApi(19)
	    private void disableReaderMode() {
	    	if(sysVersion<19)
	    		return;
	    	
	        if (nfcAdapter != null) {
	        	nfcAdapter.disableReaderMode(this);
	        }
	    }

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			if(isoDep!=null)isoDep.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}


	static int time0088 = 0;
	public static byte[] bitresult = null;
	
	private Handler myHandler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ConsantHelper.NFC_CARD_TYPE_ERR:
				Toast.makeText(cardinfo, "不支持此卡片", Toast.LENGTH_SHORT).show();
				break;
			case ConsantHelper.NFC_CARD_ERR:
				biaoti.setText("NFC读卡失败、请重新放卡");
				pb.setProgress(0);
				Toast.makeText(cardinfo, "NFC读卡失败、请重新放卡", Toast.LENGTH_SHORT).show();
				break;
			case ConsantHelper.NFC_CONNECT_ERR:
				biaoti.setText("NFC连接卡片失败、请重新放卡");
				pb.setProgress(0);
				Toast.makeText(cardinfo, "NFC连接卡片失败、请重新放卡", Toast.LENGTH_SHORT).show();
				break;
			case ConsantHelper.NET_CONNECT_SERVER_ERR:
				Toast.makeText(cardinfo, "连接服务器异常、请检查网络", Toast.LENGTH_SHORT).show();
				break;
			case ConsantHelper.NET_RECIVE_ERR:
				//接收错误，再次开启读卡流程
				Toast.makeText(cardinfo, "接收异常、请检查网络", Toast.LENGTH_SHORT).show();
				//myHandler.sendEmptyMessage(5);
				break;
			case -2:
				break;
			case -1:
				break;
			case 0:
				break;
			case 1:
				break;
			case ConsantHelper.READ_CARD_second:
				//zqdreadId.ReadBCardSecond(host, port, 5000);
				break;
			case 12:
				pb.setProgress(0);
				biaoti.setText("NFC没有检测到卡片、请重新刷卡");
				linearView.invalidate();
				Toast.makeText(cardinfo, "NFC没有检测到卡片、请重新刷卡", Toast.LENGTH_LONG).show();
				break;
			case 13:
				pb.setProgress(0);
				biaoti.setText("网络超时、请重新刷卡");
				linearView.invalidate();
				Toast.makeText(cardinfo, "网络错误、请重新刷卡", Toast.LENGTH_LONG).show();
				break;
			case 4:
				time0088++;
				if (debug)
					Log.e("hjs3", "3次----------------" + time0088);
				if (time0088 < 2) {				
					biaoti.setText("识别身份证成功，请勿移动身份证");
					zqdreadId.ReadBCardSecond(host, port, 5000);
					// socketconntinit(host, port, cmd);
				} else {
					pb.setProgress(0);
					biaoti.setText("验证失败、请重新刷卡");
					linearView.invalidate();
					Toast.makeText(cardinfo, "验证失败、请重新刷卡", Toast.LENGTH_LONG).show();
				}
				break;
			case 5:// 循环测试
				if (debug)
					Log.e("hjs2", "time==-------" + time++);

				biaoti.setText("识别身份证成功，请勿移动身份证");
				zqdreadId.ReadBCardSecond(host, port, 5000);
				break;
			case 0x10:				
				break;
			case 82:
				pb.setProgress(20);				
				break;
			case 86:
				pb.setProgress(60);
				break;
			case 88:
				break;
			case 99:
				break;
			case ConsantHelper.READ_CARD_SUCCESS:
				try {
					bitresult=(byte[]) msg.obj;   
					String testid = handleID2(0, bitresult);//解析函数1，可以用jiez里面的解析函数 decodeInfo数组
					ed02.setText(testid);
					pb.setProgress(100);
					jizp(bitresult);

					if (Test)TotalEnd = System.currentTimeMillis();
					if (Test) Log.e("hjs", "AllTime=" + (TotalEnd - ReadIDStard));

					if ((ed02 != null) && (ed02.equals(""))) {

					}
				} catch (Exception e) {
					e.printStackTrace();
					// TODO: handle exception
				}
				//myHandler.sendEmptyMessage(ConsantHelper.READ_CARD_second);
				break;			
			default:
				break;
			}

		}
	};


	static int Readflage = -99;
	static byte[] recData = new byte[1500];
	static String[] decodeInfo = new String[10];
	public void jizp(byte[] to){
		if(to==null)return;
		System.arraycopy(to, 0, recData, 0, to.length);
		try {
			bit();  			
			/*if(Readflage == 1)
			{
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(bmpPath);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Bitmap bmp = BitmapFactory.decodeStream(fis);
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				imageView10066.setImageBitmap(bmp);
			}
			else
			{
			}       */
		
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String bmpPath ;
	public static String wltPath ;

	public void decodeImagexxx(byte[] wlt) {
		try {
	            File wltFile = new File(wltPath);
	            FileOutputStream fos = new FileOutputStream(wltFile);
	            fos.write(wlt,0,wlt.length);
				fos.flush();
	            fos.close();

			int result = new DecodeUtil().Wlt2Bmp(wltPath, bmpPath);

			if (result >= 0) {
                File f = new File(bmpPath);
               // if (f.exists()) {
					Bitmap bitmap = BitmapFactory.decodeFile(bmpPath);
					mIdCardInfo.setPicBitmap(bitmap);
					imageView10066.setImageBitmap(bitmap);
					//Readflage = 1;
				//}
			} else {
			}
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}

	}



	private void bit() throws UnsupportedEncodingException {
		
		if(recData[9] == -112)
		{		
			
			byte[] dataBuf = new byte[256];								
			for(int i = 0; i < 256; i++)
			{
				dataBuf[i] = recData[14 + i];
			}
			String TmpStr = new String(dataBuf, "UTF16-LE");
			TmpStr = new String(TmpStr.getBytes("UTF-8"));
			decodeInfo[0] = TmpStr.substring(0, 15);
			decodeInfo[1] = TmpStr.substring(15, 16);
			decodeInfo[2] = TmpStr.substring(16, 18);
			decodeInfo[3] = TmpStr.substring(18, 26);
			decodeInfo[4] = TmpStr.substring(26, 61);
			decodeInfo[5] = TmpStr.substring(61, 79);
			decodeInfo[6] = TmpStr.substring(79, 94);
			decodeInfo[7] = TmpStr.substring(94, 102);
			decodeInfo[8] = TmpStr.substring(102, 110);
			decodeInfo[9] = TmpStr.substring(110, 128);
			if (decodeInfo[1].equals("1"))
				decodeInfo[1] = "男";
			else
				decodeInfo[1] = "女";
			try
			{
				int code = Integer.parseInt(decodeInfo[2].toString());
				decodeInfo[2] = decodeNation(code);
			}
			catch (Exception e)
			{
				decodeInfo[2] = "";
			}

			byte[] bmpfile = new byte[1024];
			System.arraycopy(recData, 270, bmpfile, 0, bmpfile.length);
			//writefileToSD(bmpfile);
			decodeImagexxx(bmpfile);


			/*//照片解码
			try
			{	
				int ret = IDCReaderSDK.Init();
				if (ret == 0)
				{	
					byte[] datawlt = new byte[1384];
					byte[] byLicData = {(byte)0x05,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x5B,(byte)0x03,(byte)0x33,(byte)0x01,(byte)0x5A,(byte)0xB3,(byte)0x1E,(byte)0x00};
					for(int i = 0; i < 1295; i++)
					{
					 	datawlt[i] = recData[i];
					}
					int t = IDCReaderSDK.unpack(datawlt,byLicData);

					if(t == 1)
					{
						Readflage = 1;//读卡成功
					}
					else
					{
						Readflage = 6;//照片解码异常
					}											
				}
				else
				{
					Readflage = 6;//照片解码异常
				}										
			}

			catch(Exception e)
			{								
				Readflage = 6;//照片解码异常
			}*/

			
		}
		else
		{								
			Readflage = -5;//读卡失败！
		}

			}
	private static String decodesex(String code) {
		if (code.equals("1")) {
			return "男";
		} else {
			return "女";
		}
	}

	private static String decodeNation(int code) {
		String nation;
		switch (code) {
		case 1:
			nation = "汉";
			break;
		case 2:
			nation = "蒙古";
			break;
		case 3:
			nation = "回";
			break;
		case 4:
			nation = "藏";
			break;
		case 5:
			nation = "维吾尔";
			break;
		case 6:
			nation = "苗";
			break;
		case 7:
			nation = "彝";
			break;
		case 8:
			nation = "壮";
			break;
		case 9:
			nation = "布依";
			break;
		case 10:
			nation = "朝鲜";
			break;
		case 11:
			nation = "满";
			break;
		case 12:
			nation = "侗";
			break;
		case 13:
			nation = "瑶";
			break;
		case 14:
			nation = "白";
			break;
		case 15:
			nation = "土家";
			break;
		case 16:
			nation = "哈尼";
			break;
		case 17:
			nation = "哈萨克";
			break;
		case 18:
			nation = "傣";
			break;
		case 19:
			nation = "黎";
			break;
		case 20:
			nation = "傈僳";
			break;
		case 21:
			nation = "佤";
			break;
		case 22:
			nation = "畲";
			break;
		case 23:
			nation = "高山";
			break;
		case 24:
			nation = "拉祜";
			break;
		case 25:
			nation = "水";
			break;
		case 26:
			nation = "东乡";
			break;
		case 27:
			nation = "纳西";
			break;
		case 28:
			nation = "景颇";
			break;
		case 29:
			nation = "柯尔克孜";
			break;
		case 30:
			nation = "土";
			break;
		case 31:
			nation = "达斡尔";
			break;
		case 32:
			nation = "仫佬";
			break;
		case 33:
			nation = "羌";
			break;
		case 34:
			nation = "布朗";
			break;
		case 35:
			nation = "撒拉";
			break;
		case 36:
			nation = "毛南";
			break;
		case 37:
			nation = "仡佬";
			break;
		case 38:
			nation = "锡伯";
			break;
		case 39:
			nation = "阿昌";
			break;
		case 40:
			nation = "普米";
			break;
		case 41:
			nation = "塔吉克";
			break;
		case 42:
			nation = "怒";
			break;
		case 43:
			nation = "乌孜别克";
			break;
		case 44:
			nation = "俄罗斯";
			break;
		case 45:
			nation = "鄂温克";
			break;
		case 46:
			nation = "德昂";
			break;
		case 47:
			nation = "保安";
			break;
		case 48:
			nation = "裕固";
			break;
		case 49:
			nation = "京";
			break;
		case 50:
			nation = "塔塔尔";
			break;
		case 51:
			nation = "独龙";
			break;
		case 52:
			nation = "鄂伦春";
			break;
		case 53:
			nation = "赫哲";
			break;
		case 54:
			nation = "门巴";
			break;
		case 55:
			nation = "珞巴";
			break;
		case 56:
			nation = "基诺";
			break;
		case 97:
			nation = "其他";
			break;
		case 98:
			nation = "外国血统中国籍人士";
			break;
		default:
			nation = "";
			break;
		}
		return nation;
	}

	
	
	
	/**
	 * 解析身份证信息
	 * @param len
	 * @param data
	 * @return
	 */
	public String handleID2(int len, byte[] data){
		StringBuffer sb = new StringBuffer();
//		if(((data[1]&0xff)==0xaa)||((data[2]&0xff)==0xaa)){
		if(true){
			byte[] head = new byte[8];
			byte[] xm= new byte[30];
			byte[] xb= new byte[2];
			byte[] mz= new byte[4];
			byte[] cs= new byte[16];
			byte[] zz= new byte[70];
			byte[] id= new byte[36];
			byte[] qfzg= new byte[30];
			byte[] start= new byte[16];
			byte[] end= new byte[16];
			int pos = 10-4;
			System.arraycopy(data, pos, head, 0, head.length);
			pos+=head.length;
			System.arraycopy(data, pos, xm, 0, xm.length);
			pos+=xm.length;
			System.arraycopy(data, pos, xb, 0, xb.length);
			pos+=xb.length;
			System.arraycopy(data, pos, mz, 0, mz.length);
			pos+=mz.length;
			System.arraycopy(data, pos, cs, 0, cs.length);
			pos+=cs.length;
			System.arraycopy(data, pos, zz, 0, zz.length);
			pos+=zz.length;
			System.arraycopy(data, pos, id, 0, id.length);
			pos+=id.length;
			System.arraycopy(data, pos, qfzg, 0, qfzg.length);
			pos+=qfzg.length;			
			System.arraycopy(data, pos, start, 0, start.length);
			pos+=start.length;
			System.arraycopy(data, pos, end, 0, end.length);
			pos+=end.length;
			
			xm = Util.bigtosmall(xm);
			xb = Util.bigtosmall(xb);
			mz = Util.bigtosmall(mz);
			cs = Util.bigtosmall(cs);
			zz = Util.bigtosmall(zz);
			id = Util.bigtosmall(id);
			qfzg = Util.bigtosmall(qfzg);
			start = Util.bigtosmall(start);
			end = Util.bigtosmall(end);
			
			try {
				sb.append(showinfo[0]+new String(xm,encoding)+"\n");
				sb.append(showinfo[1]+decodesex(new String(xb,encoding))+"\n");
				sb.append(showinfo[2]+decodeNation(Integer.parseInt(new String(mz,encoding)))+"\n");
				sb.append(showinfo[3]+new String(cs,encoding)+"\n");
				sb.append(showinfo[4]+new String(zz,encoding)+"\n");
				sb.append(showinfo[5]+new String(id,encoding)+"\n");
				sb.append(showinfo[6]+new String(qfzg,encoding)+"\n");
				sb.append(showinfo[7]+new String(start,encoding)+"\n");
				sb.append(showinfo[8]+new String(end,encoding)+"\n");


				//------------
				mIdCardInfo = new IdCardInfo();
				mIdCardInfo.setName(new String(xm,encoding));
				mIdCardInfo.setSex(decodesex(new String(xb,encoding)));
				mIdCardInfo.setNation(decodeNation(Integer.parseInt(new String(mz,encoding))));
				mIdCardInfo.setBirthDay(new String(cs,encoding));
				mIdCardInfo.setAddress(new String(zz,encoding));
				mIdCardInfo.setId(new String(id,encoding));
				mIdCardInfo.setIssueingUnit(new String(qfzg,encoding));
				mIdCardInfo.setStartDate(new String(start,encoding));
				mIdCardInfo.setEndDate(new String(end,encoding));
				//---------------

				if(debug) System.out.print(sb.toString());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		return sb.toString();
	}




	
	
}
