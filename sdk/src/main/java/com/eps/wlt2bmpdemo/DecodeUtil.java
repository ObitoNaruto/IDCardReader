package com.eps.wlt2bmpdemo;

public class DecodeUtil {

	public native int Wlt2Bmp(String wltPath, String bmpPath);
	
	static{
		System.loadLibrary("Wlt2Bmp");
	}
}
