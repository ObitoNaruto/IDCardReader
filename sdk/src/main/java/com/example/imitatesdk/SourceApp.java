package com.example.imitatesdk;

import android.app.Activity;
import android.app.Application;
import android.nfc.Tag;

import java.util.ArrayList;
import java.util.List;

public class SourceApp extends Application {

	public static SourceApp instance;
//	private String firApdu = "00A40000023F00";
	private String firApdu = "00A40000026002";

	private List<Activity> acList;
	
	public Tag tag;
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		acList = new ArrayList<Activity>();
	}
	
	public static SourceApp getInstance(){
		return instance;
	}
	
	public void desAcList() {
		for (int i = 0; i < acList.size(); i++) {
			if(acList.get(i) != null){
				acList.get(i).finish();
			}
		}
	}

	public void addAcList(Activity ac) {
		this.acList.add(ac);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.exit(0);
	}
}
