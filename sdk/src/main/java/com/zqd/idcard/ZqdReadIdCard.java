package com.zqd.idcard;


import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.NfcB;
import android.os.Handler;
import android.util.Log;

import com.mobile.android.idcard.sdk.handler.IHandler;
import com.mobile.android.idcard.sdk.handler.NfcBHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

public class ZqdReadIdCard {
//	public static NfcB isoDep;
	public static Tag tag;

	public static IHandler sHandler;
	
	public static boolean debug =true;

	private byte[] firApdu ={0x00,(byte) 0xA4,0x00,0x00,0x02,0x60,0x02};
	private byte[] secApdu ={(byte) 0x80,(byte) 0xB0,0x00,0x00,0x20};
	byte[] responsecod = {(byte)0x3B,(byte)0x9D,(byte)0x18,(byte)0x00,(byte)0x50,(byte)0x53,(byte)0x50,(byte)0x4d,(byte)0x12,(byte)0x03,(byte)0x01,(byte)0x32,(byte)0xff,(byte)0xeb,(byte)0x16,(byte)0x99,(byte)0xa0};
	byte[] key ={(byte)0x03,(byte)0x20,(byte)0xe9,(byte)0x23,(byte)0xb0,(byte)0xf2,(byte)0xbd,(byte)0x75,(byte)0xda,(byte)0xb5,(byte)0xc4,(byte)0xa8,(byte)0x5f,(byte)0x2f,(byte)0x18,(byte)0x0f,(byte)0x03,(byte)0x20,(byte)0xe9,(byte)0x23,(byte)0xb0,(byte)0xf2,(byte)0xbd,(byte)0x75};
	byte[] random = { 0x00, (byte) 0x84, 0x00, 0x00, 0x08 };
	byte[] manfile = null;
	
	//private static final boolean debug = false;
	
	Context cardinfo = null;
	Handler myHandler;
	/**
	 * 构造
	 * @param Context zqd 上下文
	 * @param uiHandler Handler返回结果
	 */
	public ZqdReadIdCard(Context zqd, Handler uiHandler){
		myHandler= uiHandler;
		cardinfo = zqd;
		///jiemi();
	}
	
	private void copyBigDataToSD(String asset, String strOutFileName)
    {  
		try{
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(strOutFileName);
        myInput = cardinfo.getAssets().open(asset);  
        byte[] buffer = new byte[1024];  
        int length = myInput.read(buffer);
        while(length > 0)
        {
            myOutput.write(buffer, 0, length); 
            length = myInput.read(buffer);
        }
        
        myOutput.flush();  
        myInput.close();  
        myOutput.close();     
		}catch(Exception e){
			
		}
    }
	private void jiemi(){
		File tep = android.os.Environment.getExternalStorageDirectory();
		String root = tep.toString();
		String rootFile =root+"/wltlib";
		
		File create = new File(rootFile);
		if(create.exists()){
			 create = new File(rootFile+"/base.dat");
			if(create.exists()){
				return;
			}
		}else{
			
			create.mkdir();
			copyBigDataToSD("wltlib/base.dat", rootFile+"/base.dat");
			copyBigDataToSD("wltlib/license.lic", rootFile+"/license.lic");
			
		}
	}
	/**
	 * 4.4以下或者onNewIntent 函数时调用 
	 */
	public boolean NFCWithIntent(Intent intent) {
		if(intent==null)return false;
		tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		return true;
	}
	
	/**
	 * 实现NfcAdapter.ReaderCallback时调用
	 * 
	 */
	public boolean NFCWithTag(Tag tagarg) {
		if(tagarg==null) return false;
		tag = tagarg;		
		 return true;
	}
	
	/////////////stard////////
	Long ReadIDStard = (long) 0;
	Long ReadIDEnd = (long) 0;
	Long Do0082End = (long) 0;
	Long TotalEnd = (long) 0;
	private static final boolean Test =false;
	/////////////end/////////

	byte[] fistbyte = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96,	0x69, 0x00, 0x03, (byte) 0xB5, 0x00, (byte) 0xB6 };
	byte[] head6002 = {(byte)0xAA,(byte)0xAA,(byte)0xAA,(byte)0xA5,(byte)0x5A,(byte)0x00,(byte)0x29,0x06, 0x01, 0x60, 0x02, 0x00, 0x00, 0x00 ,0x20};
	
//	String host = "20.20.1.180";
//	int port = 5002;

	public void ReadBCard(String host, int port, int timeout, IHandler deviceHandler) {
		sHandler = deviceHandler;
		ReadBCard(host, port, timeout);
	}

	/**
	 * 开始读卡流程
	 * @param host 服务器IP
	 * @param port  端口
	 * @param timeout 超时设置
	 */
	public void ReadBCard(String host, int port, int timeout) {
		try {
			if (sHandler == null) {
				if (tag == null) {
					myHandler.sendEmptyMessage(ConsantHelper.NFC_TAG_ERR);
					return;
				}
				//one.NfcB初始化
//			isoDep = NfcB.get(tag);

				sHandler = new NfcBHandler(tag);
				sHandler.init();
			}
			if (/*isoDep == null*/sHandler.get() == null) {
				myHandler.sendEmptyMessage(ConsantHelper.NFC_CARD_TYPE_ERR);
				return;
			}
			if(Test)ReadIDStard= System.currentTimeMillis();
            //two.连接
//			isoDep.connect();
			sHandler.connect();
			byte[] result = null;
			if (/*isoDep.isConnected()*/ sHandler.isConnected()) {
                //three.transceive传输指令
//				result = isoDep.transceive(firApdu);
//				result = isoDep.transceive(secApdu);

				result = sHandler.transceive(firApdu);
				result = sHandler.transceive(secApdu);

				manfile = result;
				if (manfile.length < 6) {
					myHandler.sendEmptyMessage(ConsantHelper.NFC_CARD_ERR);
				} else {
					//if(debug)Log.e("hjs", "fistbyte====");					
					byte[] cmd = new byte[head6002.length + manfile.length - 2];
					System.arraycopy(head6002, 0, cmd, 0, head6002.length);
					System.arraycopy(manfile, 0, cmd, head6002.length, manfile.length-3);

					int lrc = getLRC(cmd, 5, cmd.length);
					cmd[cmd.length - 1] = (byte) lrc;
					
					//biaoti.setText("识别身份证成功，请勿移动身份证");			
					time0088 = 0;
					//byte[] cmd2 = HandleData.hexStringToByte("AAAAAA96690026B000014841010A0115000000000003D317B2A478B77CC49ABC2F94B8DF927181BD9000000092");
					//manfile =  HandleData.hexStringToByte("B000014841010A0115000000000003D317B2A478B77CC49ABC2F94B8DF927181BD900000");
					socketconntinit(host, port, cmd,timeout);
				}
			}

		} /*catch (IOException e1) {
			myHandler.sendEmptyMessage(ConsantHelper.NFC_CONNECT_ERR);
			e1.printStackTrace();
		}*/catch (Exception e) {
			// TODO: handle exception
			myHandler.sendEmptyMessage(ConsantHelper.NFC_TAG_ERR);
		}
		finally {

		}

	}
	
	public void ReadBCardSecond(String host, int port, int timeout){
		
		if(/*isoDep.isConnected()*/sHandler.get() == null){
			try{
//			byte[] result = isoDep.transceive(firApdu);
			byte[] result = sHandler.transceive(firApdu);
			}catch(Exception e){
				myHandler.sendEmptyMessage(ConsantHelper.NFC_CONNECT_ERR);
				return;
			}
			
			
		byte[] cmd = new byte[head6002.length + manfile.length + 2];
		System.arraycopy(head6002, 0, cmd, 0, head6002.length);
		System.arraycopy(manfile, 0, cmd, head6002.length, manfile.length);

		int lrc = getLRC(cmd, 5, cmd.length);
		cmd[cmd.length - 1] = (byte) lrc;	
		socketconntinit(host, port, cmd,timeout);
		}else{
			sHandler.connect();
//			try {
////				isoDep.connect();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				myHandler.sendEmptyMessage(ConsantHelper.NFC_CONNECT_ERR);
//				e.printStackTrace();
//				return;
//			}
			
			myHandler.sendEmptyMessage(ConsantHelper.READ_CARD_second);
			
		}
	}

	
	private static byte getLRC(byte[] data, int pos, int len) {
		byte lrc = 0;
		lrc = data[pos];
		for (int i = pos + 1; i < len; i++) {
			lrc = (byte) (lrc ^ data[i]);
		}
		return lrc;
	}

	//private static ArrayList<byte[]> IDfile = new ArrayList<byte[]>();
	 byte[] apduerror = {(byte)0xAA,(byte)0xAA,(byte)0xAA,(byte)0xA5,(byte)0x5A,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x03,0x00};
	 byte[] apdu0088 = {(byte)0xAA,(byte)0xAA,(byte)0xAA,(byte)0xA5,(byte)0x5A,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x88};
	 byte[] apdu0082 = {(byte)0xAA,(byte)0xAA,(byte)0xAA,(byte)0xA5,(byte)0x5A,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x82};
	 byte[] head = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA,	(byte) 0xA5, 0x5A, 0x00, 0x0B,(byte) 0x90,0x00 };
	 byte[] file6011 = {(byte) 0xAA, (byte) 0xAA,(byte)0xAA,(byte)0x96,(byte)0x69,(byte)0x00,(byte)0x0B,(byte)0xB0,(byte)0x00,(byte)0xA4,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x60,(byte)0x11,(byte)0xFB,(byte)0xCD,(byte)0x5A};
	 byte[] apdu9000Rst = {(byte) 0x90,0x00};
	 byte[] DecFirst = {(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0x00,(byte) 0x0C,(byte) 0x60,(byte) 0x02,(byte) 0x90,(byte) 0x00,(byte) 0xFE,(byte) 0xFE,(byte) 0xFE};
	
	 byte[] headrandom = {  (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xA5, 0x5A, 0x00, 0x11, 0x06, 0x01, 0x60, 0x00, 0x00, 0x00, 0x00, 0x08};
	 int timetick = 0;// 计时器
	// AAAAAA96690013B0008200420AF0012E1C12347B82393B24396A
	 /*
	  * 处理模块返回数据0088，0082
	  */
	private void processresult(byte[] msg) {
		byte[] strf = (msg) ;

		try {
			if ((strf.length > 8) && (strf[0] == apdu0088[0])&& (strf[8] == apdu0088[8])) {
				if(Test)ReadIDEnd= System.currentTimeMillis();
				if(Test) Log.e("hjs", "88return="+(ReadIDEnd-ReadIDStard));
				
				byte[] apdu = new byte[15];
				System.arraycopy(strf, 7, apdu, 0, 15);
				
				if(debug) Log.e("hjs", "0088apdu：" + HandleData.bytesToHexString1(apdu));
//				byte[] result = isoDep.transceive(apdu);
				byte[] result = sHandler.transceive(apdu);
				if(debug) Log.e("hjs", "0088卡片返回：" + HandleData.bytesToHexString1(result));
				
				

				head[7]=0x09;
				byte[] cmd = new byte[result.length + head.length -2];
				System.arraycopy(head, 0, cmd, 0, head.length);
				System.arraycopy(result, 0, cmd, head.length, result.length-3);

				int lrc = getLRC(cmd, 5, cmd.length);
				cmd[cmd.length - 1] = (byte) lrc;				
				//socketconnt(cmd, false, 0);
				if(debug) Log.e("hjs", "0084数据：" + HandleData.bytesToHexString1(random));
//				byte[] ranresult = isoDep.transceive(random);
				byte[] ranresult = sHandler.transceive(random);
				if(debug) Log.e("hjs", "0084卡片返回：" + HandleData.bytesToHexString1(ranresult));
				
				head[7]=0x0A;
				 byte[] send02random = new byte[head.length + 9];
				System.arraycopy(head, 0, send02random, 0, head.length);
				System.arraycopy(ranresult, 0, send02random, head.length,ranresult.length-3);

				lrc = getLRC(send02random, 5, send02random.length);
				send02random[send02random.length - 1] = (byte) lrc;
				
				byte[] total = new byte[cmd.length+send02random.length];
				System.arraycopy(cmd, 0, total, 0, cmd.length);
				System.arraycopy(send02random, 0, total, cmd.length, send02random.length);
				socketconnt(total, false, 0);					
						
				socketRead();			
				//SocketUtil.SendRW(cardinfo, false, false, null, 0, send02random);
				// myHandler.sendEmptyMessage(99);
				
			} else if ((strf.length > 8) && (strf[0] == apdu0082[0])&& (strf[8] == apdu0082[8])) {
				byte[] apdu = new byte[15];
				System.arraycopy(strf, 7, apdu, 0, 15);
				if(debug) Log.e("hjs","0082=send=apdu=="+ HandleData.bytesToHexString1(apdu));
//				byte[] result = isoDep.transceive(apdu);
				byte[] result = sHandler.transceive(apdu);
				if(debug) Log.e("hjs","0082======apdu=="+ HandleData.bytesToHexString1(result));

				if(Test)Do0082End= System.currentTimeMillis();
				if(Test) Log.e("hjs", "82zhixing="+(Do0082End-ReadIDEnd));
				
				if(result[0] ==apdu9000Rst[0]){
				//socketconnt(apdu9000, false, 0);
				//socketReadDoNothing();				
				//Thread.sleep(600);
				processwy60111213();
				//myHandler.sendEmptyMessage(5);
				myHandler.sendEmptyMessage(ConsantHelper.ProgressPass);
				}else{
					myHandler.sendEmptyMessage(ConsantHelper.READ_CARD_second);
				}			
				
			} 
//			else if(((strf[9]==file6011[9])&&(strf[14]==file6011[14])&&(strf[13]==file6011[13]))){//socket发送给林刚
//				
//				process60111213();
//				
//			}
//			else if ((strf[9]==file6011[9])&&(strf[14]==file6011[14])&&(strf[13]==file6011[13])) {
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						try {
//							//socketconntinit();
//							byte[] result = isoDep.transceive(HandleData
//									.HexString2Bytes("00A40000026011"));
//							byte[] msg = socketconnt(apdu9000);
//							// AAAAAA96690009B080B0000028F489DC
//							byte[] head = HandleData.HexString2Bytes("AAAAAA966900");
//
//							byte[] subapdu = new byte[5];
//							while (((msg[8] & 0xff) == 0x80) && ((msg[9] & 0xff) == 0xB0)) {
//								System.arraycopy(msg, 8, subapdu, 0, 5);
//								int offset = subapdu[3] & 0xff;
//								int getlen = subapdu[4] & 0xff;
//								if ((offset + getlen) <= 214) {
//									result = isoDep.transceive(subapdu);
//									byte[] cmd = new byte[result.length	+ head.length + 4];
//									System.arraycopy(head, 0, cmd, 0,head.length);
//									cmd[head.length] = (byte) (result.length + 3);
//									cmd[head.length + 1] = (byte) 0xB0;
//									System.arraycopy(result, 0, cmd,head.length + 2, result.length);
//									int lrc = getLRC(cmd, 5, cmd.length);
//									cmd[cmd.length - 1] = (byte) lrc;
//									msg = socketconnt(cmd);
//									if ((offset + getlen) < 214) {
//										System.arraycopy(msg, 8, subapdu, 0, 5);
//									} else if ((offset + getlen) == 214) {
//										break;
//									}
//								}
//							}
//							pb.setProgress(60);
//							if (HandleData.bytesToHexString1(msg).startsWith("AAAAAA9669000BB000A40000026012")) {
//								result = isoDep.transceive(HandleData.HexString2Bytes("00A40000026012"));
//								msg = socketconnt(apdu9000);
//								while (((msg[8] & 0xff) == 0x80)&& ((msg[9] & 0xff) == 0xB0)) {
//									System.arraycopy(msg, 8, subapdu, 0, 5);
//									int offset = subapdu[3] & 0xff;
//									int getlen = subapdu[4] & 0xff;
//									if ((offset + getlen) <= 256) {
//										result = isoDep.transceive(subapdu);
//										byte[] cmd = new byte[result.length+ head.length + 4];
//										System.arraycopy(head, 0, cmd, 0,head.length);
//										cmd[head.length] = (byte) (result.length + 3);
//										cmd[head.length + 1] = (byte) 0xB0;
//										System.arraycopy(result, 0, cmd,head.length + 2, result.length);
//										int lrc = getLRC(cmd, 5, cmd.length);
//										cmd[cmd.length - 1] = (byte) lrc;
//										msg = socketconnt(cmd);
//										if ((offset + getlen) == 256) {
//											break;
//										}
//
//									}
//								}
//							}
//							pb.setProgress(75);
//
//							if (HandleData.bytesToHexString1(msg).startsWith("AAAAAA9669000BB000A40000026013")) {
//								result = isoDep.transceive(HandleData.HexString2Bytes("00A40000026013"));
//								msg = socketconnt(apdu9000);
//
//								while (((msg[8] & 0xff) == 0x80)&& ((msg[9] & 0xff) == 0xB0)) {
//									byte[] len = new byte[2];
//									len[0] = subapdu[2];
//									len[1] = subapdu[3];
//									int total = HandleData.twobytetoint(len)+ subapdu[4];
//									if (total <= 1024) {
//										System.arraycopy(msg, 8, subapdu, 0, 5);
//										result = isoDep.transceive(subapdu);
//										byte[] cmd = new byte[result.length
//												+ head.length + 4];
//										System.arraycopy(head, 0, cmd, 0,
//												head.length);
//										cmd[head.length] = (byte) (result.length + 3);
//										cmd[head.length + 1] = (byte) 0xB0;
//										System.arraycopy(result, 0, cmd,
//												head.length + 2, result.length);
//										int lrc = getLRC(cmd, 5, cmd.length);
//										cmd[cmd.length - 1] = (byte) lrc;
//										msg = socketconnt(cmd);
//										if (total == 1024) {
//											//processresult0088();
//											break;
//										}
//									}
//								}
//							}
//
//							pb.setProgress(85);
//
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				}).start();
//			}

		}/*catch(TagLostException e){
			myHandler.sendEmptyMessage(ConsantHelper.NFC_CARD_ERR);
		}*/catch (Exception e) {
			e.printStackTrace();
			myHandler.sendEmptyMessage(ConsantHelper.READ_CARD_second);
		}
	}
	
	
	/**
	 * 不读取指纹的函数
	 */
	 public  void processwy60111213() {	
			try {							
						byte[] head = {(byte) 0xAA,(byte) 0xAA,(byte) 0xAA,(byte) 0xA5,0x5A,0x00 ,(byte) 0xDF ,0x06, 0x01, 0x60, 0x11, 0x00, 0x00, 0x00, (byte) 0xD6};
						int pos =0;
						byte[] cmd6011 = new byte[230];//230+
						System.arraycopy(head, 0, cmd6011, 0, head.length);
						pos+=head.length;
							byte[] sel6011={0x00,(byte)0xA4,0x00,0x00,0x02,0x60,0x11};
//							byte[] result = isoDep.transceive(sel6011);
							byte[] result = sHandler.transceive(sel6011);
							//if(debug)Log.e("hjs", "re6011="+HandleData.bytesToHexString1(result));	
							byte[] filed6 = {(byte) 0x80,(byte) 0xB0,0x00,0x00,(byte) 0xD6};
//							result =isoDep.transceive(filed6);
							result =sHandler.transceive(filed6);
							//if(debug)Log.e("hjs", "re6b="+HandleData.bytesToHexString1(result));
							if(result.length>214){
								System.arraycopy(result, 0, cmd6011, pos, 214); pos+=214;
							}
							
							int lrc= getLRC(cmd6011, 5, cmd6011.length);
							cmd6011[cmd6011.length-1] = (byte) lrc;
							socketconnt(cmd6011, false,0);					
							socketReadDoNothing();					
							//		byte[] totalonepacksend = new byte[502];
							//	    int totalonepacksendoffset = 0;
							//		System.arraycopy(cmd, 0, totalonepacksend, totalonepacksendoffset,cmd.length);
							//		totalonepacksendoffset+=cmd.length;

							int longl1 = 0;
							//socketconntinit("",5001);
							//socketconnt(cmd, false,longl1);						
							//IDfile.add(cmd);
							
							byte[] head6012 = {(byte) 0xAA,(byte) 0xAA,(byte) 0xAA,(byte) 0xA5,0x5A,0x01 ,(byte) 0x09 ,0x06, 0x01, 0x60, 0x12, 0x00, 0x00, 0x01, (byte) 0x00};
							pos =0;
							byte[] cmd= new byte[272];
							System.arraycopy(head6012, 0, cmd, 0, head6012.length);
							pos+=head6012.length;
							
							//head = HandleData.HexString2Bytes("05AAAA966900");
							byte[] sel6012 ={ 0x00,(byte) 0xA4,0x00,0x00,0x02,0x60,0x12};
//							result =isoDep.transceive(sel6012);
							result =sHandler.transceive(sel6012);
							//if(debug)Log.e("hjs", "re6012="+HandleData.bytesToHexString1(result));
							//head[0] =0x05;
							
							byte[]  len6012 ={ (byte) 0x80,(byte) 0xB0,0x00,0x00,(byte) 0xF0};
//							result =isoDep.transceive(len6012);
							result =sHandler.transceive(len6012);
							//if(debug)Log.e("hjs", "re80="+HandleData.bytesToHexString1(result));
												
							if(result.length>240){
								System.arraycopy(result, 0, cmd, pos, 0xf0); pos+=0xf0;
							}
							
							
							byte[]  len601202 ={ (byte) 0x80,(byte) 0xB0,0x00,(byte) 0xF0,0x10};
//							result =isoDep.transceive(len601202);
							result =sHandler.transceive(len601202);
							//if(debug)Log.e("hjs", "re8080="+HandleData.bytesToHexString1(result));
							if(result.length>0x10){
								System.arraycopy(result, 0, cmd, pos, 0x10); pos+=0x10;
								
							}
							
							lrc= getLRC(cmd, 5, cmd.length);
							cmd[cmd.length-1] = (byte) lrc;
							socketconnt(cmd, false,longl1);					
							socketReadDoNothing();
								//		System.arraycopy(cmd, 0, totalonepacksend, totalonepacksendoffset,cmd.length);
								//		totalonepacksendoffset+=cmd.length;
								//			
								//		socketconnt(totalonepacksend, false,longl1);					
								//			socketReadDoNothing();
								//					
								//					
								//		totalonepacksend = new byte[1088];
								//		totalonepacksendoffset =0;
										
							byte[] cmd6013 = new byte[1024];pos = 0;
							
							//head = HandleData.HexString2Bytes("06AAAA966900");
							byte[] selfile6013 ={0x00,(byte) 0xA4,0x00,0x00,0x02,0x60,0x13};
							result =sHandler.transceive(selfile6013);
							//if(debug)Log.e("hjs", "re6013="+HandleData.bytesToHexString1(result));
							byte[] readb0 = {(byte) 0x80,(byte) 0xB0,0x00,0x00,(byte) 0x80};
							result =sHandler.transceive(readb0);
							if(result.length>=0x80){
								System.arraycopy(result, 0, cmd6013, pos, 0x80);pos+=0x80;
							}
							byte[] readb1 = {(byte)0x80,(byte)0xB0,(byte)0x00,(byte)0x80,(byte)0x80};
							result =sHandler.transceive(readb1);
							if(result.length>=0x80){
								System.arraycopy(result, 0, cmd6013, pos, 0x80);pos+=0x80;
							}
							
							//send6013file(cmd6013, 256 * 0);
							byte[] readb2 = {(byte)0x80,(byte)0xB0,(byte)0x01,(byte)0x00,(byte)0x80};
							result =sHandler.transceive(readb2);
							if(result.length>=0x80){
								System.arraycopy(result, 0, cmd6013, pos, 0x80);pos+=0x80;
							}
							byte[] readb3 = {(byte)0x80,(byte)0xB0,(byte)0x01,(byte)0x80,(byte)0x80};
							result =sHandler.transceive(readb3);
							if(result.length>=0x80){
								System.arraycopy(result, 0, cmd6013, pos, 0x80);pos+=0x80;
							}
							//send6013file(cmd6013, 256 * 1);
							byte[] readb4 = {(byte)0x80,(byte)0xB0,(byte)0x02,(byte)0x00,(byte)0x80};
							result =sHandler.transceive(readb4);
							if(result.length>=0x80){
								System.arraycopy(result, 0, cmd6013, pos, 0x80);pos+=0x80;
							}
							
							byte[] readb5 = {(byte)0x80,(byte)0xB0,(byte)0x02,(byte)0x80,(byte)0x80};
							result =sHandler.transceive(readb5);
							
							if(result.length>=0x80){
								System.arraycopy(result, 0, cmd6013, pos, 0x80);pos+=0x80;
							}
							
							//send6013file(cmd6013, 256 * 2);
							byte[] readb6 = {(byte)0x80,(byte)0xB0,(byte)0x03,(byte)0x00,(byte)0x80};
							result =sHandler.transceive(readb6);
							
							if(result.length>=0x80){
								System.arraycopy(result, 0, cmd6013, pos, 0x80);pos+=0x80;
							}
							//最后一包
							byte[] readb7 = {(byte)0x80,(byte)0xB0,(byte)0x03,(byte)0x80,(byte)0x80};
							result =sHandler.transceive(readb7);
						
							if(result.length>=0x80){
								System.arraycopy(result, 0, cmd6013, pos, 0x80);pos+=0x80;
							}					
							byte[] head2 = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xA5, (byte) 0x5A, (byte) 0x04,
								(byte) 0x09, (byte) 0x06, (byte) 0x00, (byte) 0x60, (byte) 0x13, (byte) 0x00, (byte) 0x00,
								(byte) 0x04, (byte) 0x00 };
						byte[] subcmd6013 = new byte[head2.length + 1024 + 1];
						int sendpos = 0;
						System.arraycopy(head2, 0, subcmd6013, 0, head2.length);
						sendpos += head2.length;
						System.arraycopy(cmd6013, 0, subcmd6013, sendpos, cmd6013.length);

						lrc = getLRC(subcmd6013, 5, subcmd6013.length);
						subcmd6013[subcmd6013.length - 1] = (byte) lrc;

						socketconnt(subcmd6013, false, longl1);
						socketRead();
					
			} catch (Exception e1) {
				myHandler.sendEmptyMessage(2);
				e1.printStackTrace();
			}
		}
	
	/**
	 * 带读取指纹的函数
	 */
	 /*
	 public  void processwy60111213() {	
	try {							
				byte[] head = {(byte) 0xAA,(byte) 0xAA,(byte) 0xAA,(byte) 0xA5,0x5A,0x00 ,(byte) 0xDF ,0x06, 0x01, 0x60, 0x11, 0x00, 0x00, 0x00, (byte) 0xD6};
				int pos =0;
				byte[] cmd6011 = new byte[230];//230+
				System.arraycopy(head, 0, cmd6011, 0, head.length);
				pos+=head.length;
					byte[] sel6011={0x00,(byte)0xA4,0x00,0x00,0x02,0x60,0x11};
					byte[] result = isoDep.transceive(sel6011);
					//if(debug)Log.e("hjs", "re6011="+HandleData.bytesToHexString1(result));	
					byte[] filed6 = {(byte) 0x80,(byte) 0xB0,0x00,0x00,(byte) 0xD6};
					result =isoDep.transceive(filed6);
					//if(debug)Log.e("hjs", "re6b="+HandleData.bytesToHexString1(result));
					if(result.length>214){
						System.arraycopy(result, 0, cmd6011, pos, 214); pos+=214;							
					}
					
					int lrc= getLRC(cmd6011, 5, cmd6011.length);
					cmd6011[cmd6011.length-1] = (byte) lrc;
					socketconnt(cmd6011, false,0);					
					socketReadDoNothing();					
					//		byte[] totalonepacksend = new byte[502];
					//	    int totalonepacksendoffset = 0;
					//		System.arraycopy(cmd, 0, totalonepacksend, totalonepacksendoffset,cmd.length);
					//		totalonepacksendoffset+=cmd.length;

					int longl1 = 0;
					//socketconntinit("",5001);
					//socketconnt(cmd, false,longl1);						
					//IDfile.add(cmd);
					
					byte[] head6012 = {(byte) 0xAA,(byte) 0xAA,(byte) 0xAA,(byte) 0xA5,0x5A,0x01 ,(byte) 0x09 ,0x06, 0x01, 0x60, 0x12, 0x00, 0x00, 0x01, (byte) 0x00};
					pos =0;
					byte[] cmd= new byte[272];
					System.arraycopy(head6012, 0, cmd, 0, head6012.length);
					pos+=head6012.length;
					
					//head = HandleData.HexString2Bytes("05AAAA966900");
					byte[] sel6012 ={ 0x00,(byte) 0xA4,0x00,0x00,0x02,0x60,0x12};
					result =isoDep.transceive(sel6012);
					//if(debug)Log.e("hjs", "re6012="+HandleData.bytesToHexString1(result));
					//head[0] =0x05;
					
					byte[]  len6012 ={ (byte) 0x80,(byte) 0xB0,0x00,0x00,(byte) 0xF0};
					result =isoDep.transceive(len6012);
					//if(debug)Log.e("hjs", "re80="+HandleData.bytesToHexString1(result));
										
					if(result.length>240){
						System.arraycopy(result, 0, cmd, pos, 0xf0); pos+=0xf0;						
					}
					
					
					byte[]  len601202 ={ (byte) 0x80,(byte) 0xB0,0x00,(byte) 0xF0,0x10};
					result =isoDep.transceive(len601202);
					//if(debug)Log.e("hjs", "re8080="+HandleData.bytesToHexString1(result));
					if(result.length>0x10){
						System.arraycopy(result, 0, cmd, pos, 0x10); pos+=0x10;
						
					}
					
					lrc= getLRC(cmd, 5, cmd.length);
					cmd[cmd.length-1] = (byte) lrc;
					socketconnt(cmd, false,longl1);					
					socketReadDoNothing();
						//		System.arraycopy(cmd, 0, totalonepacksend, totalonepacksendoffset,cmd.length);
						//		totalonepacksendoffset+=cmd.length;
						//			
						//		socketconnt(totalonepacksend, false,longl1);					
						//			socketReadDoNothing();
						//					
						//					
						//		totalonepacksend = new byte[1088];
						//		totalonepacksendoffset =0;
								
					byte[] cmd6013 = new byte[1024];pos = 0;
					
					//head = HandleData.HexString2Bytes("06AAAA966900");
					byte[] selfile6013 ={0x00,(byte) 0xA4,0x00,0x00,0x02,0x60,0x13};
					result =isoDep.transceive(selfile6013);
					//if(debug)Log.e("hjs", "re6013="+HandleData.bytesToHexString1(result));
					byte[] readb0 = {(byte) 0x80,(byte) 0xB0,0x00,0x00,(byte) 0x80};
					result =isoDep.transceive(readb0);
					if(result.length>=0x80){
						System.arraycopy(result, 0, cmd6013, pos, 0x80);pos+=0x80;
					}
					byte[] readb1 = {(byte)0x80,(byte)0xB0,(byte)0x00,(byte)0x80,(byte)0x80};
					result =isoDep.transceive(readb1);
					if(result.length>=0x80){
						System.arraycopy(result, 0, cmd6013, pos, 0x80);pos+=0x80;
					}
					
					//send6013file(cmd6013, 256 * 0);
					byte[] readb2 = {(byte)0x80,(byte)0xB0,(byte)0x01,(byte)0x00,(byte)0x80};
					result =isoDep.transceive(readb2);	
					if(result.length>=0x80){
						System.arraycopy(result, 0, cmd6013, pos, 0x80);pos+=0x80;
					}
					byte[] readb3 = {(byte)0x80,(byte)0xB0,(byte)0x01,(byte)0x80,(byte)0x80};
					result =isoDep.transceive(readb3);
					if(result.length>=0x80){
						System.arraycopy(result, 0, cmd6013, pos, 0x80);pos+=0x80;
					}
					//send6013file(cmd6013, 256 * 1);
					byte[] readb4 = {(byte)0x80,(byte)0xB0,(byte)0x02,(byte)0x00,(byte)0x80};
					result =isoDep.transceive(readb4);
					if(result.length>=0x80){
						System.arraycopy(result, 0, cmd6013, pos, 0x80);pos+=0x80;
					}
					
					byte[] readb5 = {(byte)0x80,(byte)0xB0,(byte)0x02,(byte)0x80,(byte)0x80};
					result =isoDep.transceive(readb5);						
					
					if(result.length>=0x80){
						System.arraycopy(result, 0, cmd6013, pos, 0x80);pos+=0x80;
					}
					
					//send6013file(cmd6013, 256 * 2);
					byte[] readb6 = {(byte)0x80,(byte)0xB0,(byte)0x03,(byte)0x00,(byte)0x80};
					result =isoDep.transceive(readb6);
					
					if(result.length>=0x80){
						System.arraycopy(result, 0, cmd6013, pos, 0x80);pos+=0x80;
					}
					//最后一包
					byte[] readb7 = {(byte)0x80,(byte)0xB0,(byte)0x03,(byte)0x80,(byte)0x80};
					result =isoDep.transceive(readb7);
				
					if(result.length>=0x80){
						System.arraycopy(result, 0, cmd6013, pos, 0x80);pos+=0x80;
					}					
					//	socketconnt(cmd6011, false,0);					
					//	socketReadDoNothing();	
					//	socketconnt(cmd, false,0);					
					//	socketReadDoNothing();
					
					byte[] selfile6021 ={0x00,(byte) 0xA4,0x00,0x00,0x02,0x60,0x21};///指纹信息
					result =isoDep.transceive(selfile6021);
					if (debug | true)Log.e("hjs", "re6021=" + HandleData.bytesToHexString1(result));
					
					byte[] readb021Test = { (byte) 0x80, (byte) 0xB0, 0x00, 0x00, (byte) 0x80 };				
					byte[] fingerdata = isoDep.transceive(readb021Test);
					
			if ((result[0] != apdu9000Rst[0])||(fingerdata[0]==0)||(fingerdata[1]==0)||(fingerdata[2]==0)||(fingerdata[3]==0)||(fingerdata[4]==0)||(fingerdata[5]==0)||(fingerdata[6]==0)) {
				byte[] head2 = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xA5, (byte) 0x5A, (byte) 0x04,
						(byte) 0x09, (byte) 0x06, (byte) 0x00, (byte) 0x60, (byte) 0x13, (byte) 0x00, (byte) 0x00,
						(byte) 0x04, (byte) 0x00 };
				byte[] subcmd6013 = new byte[head2.length + 1024 + 1];
				int sendpos = 0;
				System.arraycopy(head2, 0, subcmd6013, 0, head2.length);
				sendpos += head2.length;
				System.arraycopy(cmd6013, 0, subcmd6013, sendpos, cmd6013.length);

				lrc = getLRC(subcmd6013, 5, subcmd6013.length);
				subcmd6013[subcmd6013.length - 1] = (byte) lrc;

				socketconnt(subcmd6013, false, longl1);
				socketRead();

			} else {
				
				byte[] head2 = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xA5, (byte) 0x5A, (byte) 0x04,
						(byte) 0x09, (byte) 0x06, (byte) 0x00, (byte) 0x60, (byte) 0x13, (byte) 0x60, (byte) 0x21,
						(byte) 0x04, (byte) 0x00 };
				byte[] subcmd6013 = new byte[head2.length + 1024 + 1];
				int sendpos = 0;
				System.arraycopy(head2, 0, subcmd6013, 0, head2.length);
				sendpos += head2.length;
				System.arraycopy(cmd6013, 0, subcmd6013, sendpos, cmd6013.length);

				lrc = getLRC(subcmd6013, 5, subcmd6013.length);
				subcmd6013[subcmd6013.length - 1] = (byte) lrc;

				socketconnt(subcmd6013, false, longl1);
				socketReadDoNothing();
				
				
				byte[] cmd6021 = new byte[1024];pos = 0;				
				byte[] readb021 = { (byte) 0x80, (byte) 0xB0, 0x00, 0x00, (byte) 0x80 };				
				result = isoDep.transceive(readb021);
				if(result.length>=0x80){
					System.arraycopy(result, 0, cmd6021, pos, 0x80);pos+=0x80;
				}
				
				
				if (debug | true)Log.e("hjs", "re6021=" + HandleData.bytesToHexString1(result));
				byte[] readb121 = { (byte) 0x80, (byte) 0xB0, (byte) 0x00, (byte) 0x80, (byte) 0x80 };
				result = isoDep.transceive(readb121);
				if(result.length>=0x80){
					System.arraycopy(result, 0, cmd6021, pos, 0x80);pos+=0x80;
				}
				
				if (debug | true)Log.e("hjs", "re6021=" + HandleData.bytesToHexString1(result));
				byte[] readb221 = { (byte) 0x80, (byte) 0xB0, (byte) 0x01, (byte) 0x00, (byte) 0x80 };
				result = isoDep.transceive(readb221);
				if(result.length>=0x80){
					System.arraycopy(result, 0, cmd6021, pos, 0x80);pos+=0x80;
				}
				
				if (debug | true)Log.e("hjs", "re6021=" + HandleData.bytesToHexString1(result));
				byte[] readb321 = { (byte) 0x80, (byte) 0xB0, (byte) 0x01, (byte) 0x80, (byte) 0x80 };
				result = isoDep.transceive(readb321);
				if(result.length>=0x80){
					System.arraycopy(result, 0, cmd6021, pos, 0x80);pos+=0x80;
				}
				
				if (debug | true)Log.e("hjs", "re6021=" + HandleData.bytesToHexString1(result));
				// send6013file(cmd6013, 256 * 1);
				byte[] readb421 = { (byte) 0x80, (byte) 0xB0, (byte) 0x02, (byte) 0x00, (byte) 0x80 };
				result = isoDep.transceive(readb421);
				if(result.length>=0x80){
					System.arraycopy(result, 0, cmd6021, pos, 0x80);pos+=0x80;
				}
				
				if (debug | true)Log.e("hjs", "re6021=" + HandleData.bytesToHexString1(result));
				byte[] readb521 = { (byte) 0x80, (byte) 0xB0, (byte) 0x02, (byte) 0x80, (byte) 0x80 };
				result = isoDep.transceive(readb521);
				if(result.length>=0x80){
					System.arraycopy(result, 0, cmd6021, pos, 0x80);pos+=0x80;
				}
				
				if (debug | true)Log.e("hjs", "re6021=" + HandleData.bytesToHexString1(result));
				byte[] readb621 = { (byte) 0x80, (byte) 0xB0, (byte) 0x03, (byte) 0x00, (byte) 0x80 };
				result = isoDep.transceive(readb621);
				if(result.length>=0x80){
					System.arraycopy(result, 0, cmd6021, pos, 0x80);pos+=0x80;
				}
				
				if (debug | true)Log.e("hjs", "re6021=" + HandleData.bytesToHexString1(result));

				byte[] readb721 = { (byte) 0x80, (byte) 0xB0, (byte) 0x03, (byte) 0x80, (byte) 0x80 };
				result = isoDep.transceive(readb721);
				if (debug | true)Log.e("hjs", "re6021==" + HandleData.bytesToHexString1(result));
				if(result.length>=0x80){
					System.arraycopy(result, 0, cmd6021, pos, 0x80);pos+=0x80;
				}
				
				byte[] head21 = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xA5, (byte) 0x5A, (byte) 0x04,
						(byte) 0x09, (byte) 0x06, (byte) 0x00, (byte) 0x60, (byte) 0x21, (byte) 0x00, (byte) 0x00,
						(byte) 0x04, (byte) 0x00 };
				byte[] subcmd6021 = new byte[head21.length + 1024 + 1];
				sendpos = 0;
				System.arraycopy(head21, 0, subcmd6021, 0, head21.length);
				sendpos += head21.length;
				System.arraycopy(cmd6021, 0, subcmd6021, sendpos, cmd6021.length);

				lrc = getLRC(subcmd6021, 5, subcmd6021.length);
				subcmd6021[subcmd6021.length - 1] = (byte) lrc;

				socketconnt(subcmd6021, false, longl1);
				socketRead();
				
				
			}
					
					
					
//		for (int i = 0; i < cmd6013.length; i += 256) {
//			if (i == 256 * 3) {
//				byte[] head2 = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA,
//						(byte) 0xA5, (byte) 0x5A, (byte) 0x01, (byte) 0x09,
//						(byte) 0x06, (byte) 0x00, (byte) 0x60, (byte) 0x13,
//						(byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00 };
//				byte[] offset = HandleData.int22byte(i);
//				head2[11] = offset[0];
//				head2[12] = offset[1];
//				byte[] subcmd6013 = new byte[head2.length + 257];
//				int sendpos = 0;
//				System.arraycopy(head2, 0, subcmd6013, 0, head2.length);
//				sendpos += head2.length;
//				System.arraycopy(cmd6013, i, subcmd6013, sendpos, 256);
//
//				lrc = getLRC(subcmd6013, 5, subcmd6013.length);
//				subcmd6013[subcmd6013.length - 1] = (byte) lrc;
//				pb.setProgress(pb.getProgress()+2);
//				System.arraycopy(subcmd6013, 0, totalonepacksend, totalonepacksendoffset,subcmd6013.length);
//				totalonepacksendoffset+=subcmd6013.length;
//				
//				socketconnt(totalonepacksend, false, longl1);
//				socketRead();
//			} else {
//				byte[] head2 = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA,
//						(byte) 0xA5, (byte) 0x5A, (byte) 0x01, (byte) 0x09,
//						(byte) 0x06, (byte) 0x01, (byte) 0x60, (byte) 0x13,
//						(byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00 };
//				byte[] offset = HandleData.int22byte(i);
//				head2[11] = offset[0];
//				head2[12] = offset[1];
//				byte[] subcmd6013 = new byte[head2.length + 257];
//				int sendpos = 0;
//				System.arraycopy(head2, 0, subcmd6013, 0, head2.length);
//				sendpos += head2.length;
//				System.arraycopy(cmd6013, i, subcmd6013, sendpos, 256);
//
//				lrc = getLRC(subcmd6013, 5, subcmd6013.length);
//				subcmd6013[subcmd6013.length - 1] = (byte) lrc;
////				socketconnt(subcmd6013, false, longl1);
////				socketReadDoNothing();
//				
////				System.arraycopy(subcmd6013, 0, totalonepacksend, totalonepacksendoffset,subcmd6013.length);
////				totalonepacksendoffset+=subcmd6013.length;
//				
//				pb.setProgress(pb.getProgress()+2);
//			}
//		}
			
	} catch (Exception e1) {
		myHandler.sendEmptyMessage(2);
		e1.printStackTrace();
	}
}
*/

	public void send6013file(final byte[] cmd6013, final int i) {
				byte[] head2 = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xA5, (byte) 0x5A, (byte) 0x01,
						(byte) 0x09, (byte) 0x06, (byte) 0x01, (byte) 0x60, (byte) 0x13, (byte) 0x00, (byte) 0x00,
						(byte) 0x01, (byte) 0x00 };
				byte[] offset = HandleData.int22byte(i);
				head2[11] = offset[0];
				head2[12] = offset[1];
				byte[] subcmd6013 = new byte[head2.length + 257];
				int sendpos = 0;
				System.arraycopy(head2, 0, subcmd6013, 0, head2.length);
				sendpos += head2.length;
				System.arraycopy(cmd6013, i, subcmd6013, sendpos, 256);

				int lrc = getLRC(subcmd6013, 5, subcmd6013.length);
				subcmd6013[subcmd6013.length - 1] = (byte) lrc;
				socketconnt(subcmd6013, false, 0);
				socketReadDoNothing();
	}



	

	
	protected void onDestroy() {
		// TODO Auto-generated method stub
		try {
            //four.close
			if (sHandler != null)
				sHandler.close();
			if (out != null)
				out.close();
			if (Input != null)
				Input.close();
			if (socket != null && (socket.isConnected())) {
				socket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	static DataOutputStream out = null;
	static DataInputStream Input = null;
	static byte[] arrayMessage = new byte[1500];
	static int time0088 = 0;
	static int stats6d00 = 0;
	

	static Socket socket;

	private void socketconntinit(final String host, final int port,
                                 final byte[] send, final int timeout) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				socket = new Socket();
				try {
					socket.connect(new InetSocketAddress(host, port), timeout);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					myHandler.sendEmptyMessage(ConsantHelper.NET_CONNECT_SERVER_ERR);
					e1.printStackTrace();
				}
				try {
					socket.setSoTimeout(timeout);
					socket.setTcpNoDelay(true);
					out = new DataOutputStream(socket.getOutputStream());
					Input = new DataInputStream(socket.getInputStream());
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					myHandler.sendEmptyMessage(ConsantHelper.NET_CONNECT_SERVER_ERR);
					e.printStackTrace();
				}

				sendcardtran(send);
				socketRead();
				myHandler.sendEmptyMessage(ConsantHelper.Progress);
			}
		}).start();
	}
	 /*
	 * 发送卡片二进制指令
	 */
	private  void sendcardtran(byte[] cmd,long wait){
		byte[] subcmd = new byte[cmd.length-1];	
		System.arraycopy(cmd, 0, subcmd, 0, cmd.length-1);
		
		byte[] bluecmd = new byte[subcmd.length+10];
		bluecmd[0] = (byte)0xFF; 
		bluecmd[1] = (byte)0xFF; 
		bluecmd[2] = (byte)0xFF; 
		byte[] len =  Util.intTo2byte(subcmd.length+10); 		
		bluecmd[3] = len[0]; 
		bluecmd[4] = len[1]; 
		bluecmd[5] = 0x50; 
		bluecmd[6] = 0x01; 
		int cmdlen = subcmd.length;
		System.arraycopy(subcmd, 0, bluecmd, 7, cmdlen);
		
		bluecmd[subcmd.length+7] = getLRC(bluecmd,3, cmdlen+4);
		bluecmd[subcmd.length+8] = (byte)0xfe;
		bluecmd[subcmd.length+9] = (byte)0xfe;
		//SocketUtil.Send(cardinfo, true, false,null, 0, bluecmd);
		
//		TCPServer.write(bluecmd);
//		TCPServer.read();
		socketconnt(bluecmd, false, 0);
		socketRead();
	}
	private  void sendcardtran(byte[] sendbyte) {
		if (sendbyte != null && (out != null)) {
			try {
				out.write(sendbyte);
				// out.flush();
				if(debug) Log.e("hjs", "send=" + HandleData.bytesToHexString1(sendbyte));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				myHandler.sendEmptyMessage(ConsantHelper.NET_SEND_ERR);
				e.printStackTrace();
			}
		}
	}

	private void socketRead() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if ((Input != null)) {
					try {
						int nLen = 0;
						Arrays.fill(arrayMessage, (byte) 0);
						nLen = Input.read(arrayMessage);
						if(nLen<=0){
							myHandler.sendEmptyMessage(ConsantHelper.READ_CARD_second);
							return;
						}
						byte[] msg = new byte[nLen];
						System.arraycopy(arrayMessage, 0, msg, 0, nLen);
						if(true) Log.e("hjs","msg==" + HandleData.bytesToHexString1(msg));
						if (nLen < 1000) {
							processresult(msg);
						} else if (nLen > 1000) {
							//Message rstmsg2 = new Message();
							//rstmsg2.what = 0x20;
							//rstmsg2.obj =msg;
							myHandler.obtainMessage(ConsantHelper.READ_CARD_SUCCESS, msg).sendToTarget();
							//myHandler.sendMessage(rstmsg2);
							onDestroy();
						}
					} catch (Exception e) {
						myHandler.sendEmptyMessage(ConsantHelper.NET_RECIVE_ERR);
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	
	private void socketReadDoNothing() {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
				// TODO Auto-generated method stub
				if ((Input != null)) {
					try {
						int nLen = 0;
						Arrays.fill(arrayMessage, (byte) 0);
						nLen = Input.read(arrayMessage);
						if(nLen<=0){
							myHandler.sendEmptyMessage(ConsantHelper.READ_CARD_second);
							return;
						}
						byte[] msg = new byte[nLen];
						System.arraycopy(arrayMessage, 0, msg, 0, nLen);
						//if(debug)Log.e("hjs","doNomsg==" + HandleData.bytesToHexString1(msg));						
					} catch (Exception e) {
						myHandler.sendEmptyMessage(ConsantHelper.NET_RECIVE_ERR);
						e.printStackTrace();
					}
				}
//			}
//		}).start();
	}
	

	private byte[] socketconnt(byte[] sendbyte, boolean isreadrst, long time) {
		if (sendbyte != null && (out != null)) {
			try {
				out.write(sendbyte);
				out.flush();
				if(debug) Log.e("hjs", "send=" + HandleData.bytesToHexString1(sendbyte));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				myHandler.sendEmptyMessage(ConsantHelper.NET_SEND_ERR);
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
}
