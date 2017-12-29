package com.mobile.android.idcard.sdk;

import android.app.Activity;
import android.app.Application;
import android.nfc.Tag;

import com.example.imitatesdk.SourceApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mi on 17-12-7.
 */

public class IdCardApplication extends Application {

    private static IdCardApplication sInstance;

    public static SourceApp instance;
    //	private String firApdu = "00A40000023F00";
    private String firApdu = "00A40000026002";

    private List<Activity> acList;

    public Tag tag;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        acList = new ArrayList<Activity>();
    }

    public static IdCardApplication getInstance() {
        return sInstance;
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
