package com.eps.wlt2bmpdemo;/*
package com.eps.wlt2bmpdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.imitatesdk.R;
import com.ivsign.android.IDCReader.IDCReaderSDK;
import com.lzw.qlhsshare.Wlt2bmpShare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

*/
/**
 * 
 * @author Administrator
 * 需要将assets下面的wlt复制到sd卡根目录，然后在解密的wlt路径下配置这个wlt的路径
 * bmp路径配置的是生成的bmp的路径
 * 另外，wlt的图片解码库不支持64位的手机，如果放到64位手机上，解出来的图片可能是黑乎乎的
 *//*

public class MainActivity extends Activity {
	EditText pathEt1,pathEt2;
	ImageView image,image2;
	String TAG="MainActivity";
	static Context appcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    public void initView(){
    	pathEt1 = (EditText) findViewById(R.id.path1);
    	pathEt2 = (EditText) findViewById(R.id.path2);
    	image = (ImageView) findViewById(R.id.image);
		image2 = (ImageView) findViewById(R.id.image2);
		Button conver = (Button)findViewById(R.id.convert2);
		conver.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				convert2(null);
			}
		});

    	appcon =getApplicationContext();
    	 jiemi();
		//jiemi2();
    	 
    	 bmpPath = appcon.getFilesDir()+ "/wltlib/photon.bmp";
    	 wltPath = appcon.getFilesDir()+"/wltlib/photo.wlt";

		//bmpPath = Environment.getExternalStorageDirectory()+ "/wltlib/photon.bmp";
		//wltPath = Environment.getExternalStorageDirectory()+"/wltlib/photo.wlt";

		//wltPath = Environment.getExternalStorageDirectory()+"/wltlib/photo.wlt";


    	pathEt1.setText(wltPath);
    	pathEt2.setText(bmpPath);
    }

    
    private void copyBigDataToSD(String asset, String strOutFileName)
    {  
		try{
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(strOutFileName);
        myInput = getApplicationContext().getAssets().open(asset);  
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
		File tep = 	getApplicationContext().getFilesDir();
		//File tep = Environment.getExternalStorageDirectory();
		String root = tep.toString();
		String rootFile =root+"/wltlib";
		
		File create = new File(rootFile);
		if(create.exists()){
			 create = new File(rootFile+"/photo.wlt");
			if(create.exists()){
				return;
			}else{
				copyBigDataToSD("photo.wlt", rootFile+"/photo.wlt");
			}
		}else{			
			create.mkdir();				 
			copyBigDataToSD("photo.wlt", rootFile+"/photo.wlt");
			
		}
	}


	private void jiemi2(){
		File tep = Environment.getExternalStorageDirectory();
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

    
    public static String bmpPath ;
    public static String wltPath ;

//    public static void decodeImagexxx(byte[] wlt) {
////        if (wlt == null) {
////            return;
////        }
//        try {
////            File wltFile = new File(wltPath);
////            FileOutputStream fos = new FileOutputStream(wltFile);
////            fos.write(wlt);
////            fos.close();
//
//            DecodeWlt dw = new DecodeWlt();
//            int result = dw.Wlt2Bmp(wltPath, bmpPath);
//
//            if (result == 1) {
//                File f = new File(bmpPath);
//                if (f.exists()){
//                	
//                }
//                   
//                else {
////                    imageViewPhoto.setImageResource(R.drawable.photo);
//                }
//            } else {
////                imageViewPhoto.setImageResource(R.drawable.photo);
//            }
//        } catch (Exception ioe) {
//            ioe.printStackTrace();
//        }
//
//    }
    
    //点击转换
    public void convert(View view){
    	Log.e(TAG,"------------------convert(View view)");
    	String wltPath=pathEt1.getText().toString().trim();
    	String bmpPath = pathEt2.getText().toString().trim();
    	Log.e(TAG,"wltPath="+wltPath);
      	Log.e(TAG,"bmpPath="+bmpPath);
      	if(new File(wltPath).exists()){
      		Log.e(TAG,"------wlt文件存在");
      	}else{
      		Log.e(TAG,"------wlt文件不存在");
      	}
      	
    	int ret = new DecodeUtil().Wlt2Bmp(wltPath, bmpPath);
    	if(ret>=0){
    		Bitmap bitmap = BitmapFactory.decodeFile(bmpPath);
    		image.setImageBitmap(bitmap);
    	}else{
    		Toast.makeText(this, "解码图片失败！", Toast.LENGTH_SHORT).show();
    	}
    	Log.e(TAG,"ret="+ret);
    }


	public  byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		if (hexString.length() % 2 != 0) {
			hexString = hexString + "0";
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

		}
		return d;
	}

	private  byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
	//点击转换
	public  void convert2(View view){
    	String idmsg = "AAAAAAA55A050800009001000400AF4F9B51055E20002000200020002000200020002000200020002000200031003000310031003900380038003000320030003200B36C575301779767DE5D025E1C4EDA5947952D8CB65B51679390B65BEA813671516718FF10FFF7532000200020002000200020002000200020002000200020002000200020003400310030003500320031003100390038003800300032003000320034003500350058009767DE5D025E6C51895B405C2000200020002000200020002000200020003200300031003200300036003300300032003000320032003000360033003000200020002000200020002000200020002000200020002000200020002000200020002000574C66007E00320000FF85075151513E710DD564F2687B97839FE3F94C54E95D299422E5AE516DF43EE12C935EC059C16849570E872C4DC76CE5FED7BD772DCD859B23769D8743B07A740C204CDCBBF8639D221C0471587EFD366708B438BB2C3E0DDCD1AE2C5251515A3E9C76F333DDDABAFF2BE7594A42E18134FA2EE849FD82A2782E7BABF36CBA851EEAB9406B545AC7B908D72403BFF4C7F8D5D4E59AF6CFC54136254B7A333B80CB813149D444B96AAC667B916FE14B8AEC6D2F23CEA3D3D9F780C8B229FDC5768ACF1DA16BD2B1846E6D61244B7043676304909EB0AF7E8E72213074309018E11C523A567DA07E5A35E1A5A307CA9632F40EFABE2E8B5237FB10D0553E40DA88C16DC79359B79234A059891B4C6FE1AF2114824D0462739F6CD7B363AA635CB760B6ACEEA5DDD3FFBDC27D7782FA1C5A2BD285D781F727C1A72C57806345301EF6FC7D9AAA975CD4F770B9C221ECF76987EC1FE4FF8A3B1E5258C4235808A72FB2AE51AC81BC58D2D57E53B4CD5E0B359A520461DB7E8BDEEA577B12373C0541E37D4E04C8303C71A452CE732A5E57383BA560F9D6EF26D3FF68F349C47B8753C0557E74B3FB9295942B3367A453C4C7DA2DF27892918AEFC11D6F7AC01DAE51DD0A1E8137141208B1BED671BE71435F9943BEF97E8F0E1131DCCC5E71226B891E0217489DD242491C767E15E4AE5104F37ED3FB89EE2658FCD01DEDA388BE18214277122062F41D9273431A89C7D3CF98D4DECF29EDC2370D9AC2E36BB260653BAA2C5ED3AB2DF36F2146DBAE51161BFB7328242B2DD44C228C6F0636DEA43996026857B563E46763A7B243C5B5C4826A40A559D41F72E33045F7034384D8AE516950AE51FE1C67171133E3DB41BC7C16941C943B567548C854269DA3C144DFFF14C3E94B4C7F8CAE51C3299860DE34494F375A6743094A6AAB19A9DAF2CFC88B9DA57E69AF97D0F6BA09270C14617472F0569F9648E5688975B9396A6FF1ADFFAAAAA831B89692C8CDFEC7E117B803F541CFBC43B148D141CF3A6611002C22B9B25B4F10608B8C70AF00FCF9FFDD26263DD959B195162BCBAF5E17CC92E445483C97DDBC226F5B9A29783AD6145682C0119E32F44D5A447184B9A3085DE9E6861EFB26E835A08E0F3180A4AA74EF558A7F78AFA3E116E827F82080B104269E9B42D279DAEB588C317D644D18060EF3628D9965DB41BDA9A57B76D25A3E09024A5B657FE8C786AB342C55F1E8490142225BAF4F86E424A0660CBBC45C36604F9010BC68367F89087A503FB5E682DC696343A8D86F926E027980DFC4010EC87A0399B209364D97918F0FA0154F5A3E88DC4D83AA1445D0C99EAE51A673BEFC399E0605EC559F6E8B42A2EC3F3EDE29EBAD3C3DE9244F73573EF8B0C460BC1EC336F67B934630A96427A7A0E5F2D3DA02509DC65D29077C";

    	byte[] recData = hexStringToBytes(idmsg);

		//Bitmap bmp  = getbitmap(recData);
		//image2.setImageBitmap(bmp);

    	int Readflage =1;
		try
		{
			int ret = IDCReaderSDK.Init();
			if (ret == 0)
			{
				byte[] datawlt = new byte[1384];
				byte[] byLicData = {(byte)0x05,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x5B,(byte)0x03,(byte)0x33,(byte)0x01,(byte)0x5A,(byte)0xB3,(byte)0x1E,(byte)0x00};
//				for(int i = 0; i < 1295; i++)
//				{
//					datawlt[i] = recData[i];
//				}
				System.arraycopy(recData,0,datawlt,0,recData.length);

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
		}catch(Exception e)
		{
			Readflage = 6;//照片解码异常
		}


		if(Readflage == 1)
		{
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(Environment.getExternalStorageDirectory() + "/wltlib/zp.bmp");
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
			image2.setImageBitmap(bmp);
		}

	}

	private Bitmap getbitmap(byte[] paramArrayOfByte)
	{
		int i = 306;
		int j = 38556;
		byte[] arrayOfByte1 = new byte[40960];
		try
		{
			int k = Wlt2bmpShare.picUnpack(paramArrayOfByte, arrayOfByte1);
			if (k != 1)
				return null;
			byte[] arrayOfByte2 = new byte[38556];
			System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, 38556);
			return Tool.createRgbBitmap(arrayOfByte2, 102, 126);
		}
		catch (Exception localException)
		{
			localException.printStackTrace();
		}
		return null;
	}

}


*/
