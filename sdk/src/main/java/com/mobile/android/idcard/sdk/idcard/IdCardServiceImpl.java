package com.mobile.android.idcard.sdk.idcard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.imitatesdk.CardInfoActivity;
import com.mobile.android.idcard.sdk.IdCardApplication;
import com.mobile.android.idcard.sdk.handler.IHandler;
import com.mobile.android.idcard.sdk.util.ActivityUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class IdCardServiceImpl implements IIdCardService {
    private static final String ID_CARD = "IdCard";

    private AtomicInteger mAtomicIndex;

    public IdCardServiceImpl(){
        mAtomicIndex = new AtomicInteger(0);
    }

    @Override
    public void readIdCard(IIdCardManagerResponse response, Bundle bundle, IHandler deviceHandler) {
        if (response == null) {
            return;
        }
        if (bundle == null) {
            bundle = new Bundle();
        }

        String contextKey = generateContextKey();
        bundle.putString(IdCardConstant.KEY_CONTEXT_INDEX, contextKey);
        IdCardContext idCardContext = IdCardContext.get(contextKey);
        idCardContext.setResponse(response);
        idCardContext.setDeviceHandler(deviceHandler);
        Intent intent = new Intent(IdCardApplication.getInstance(), CardInfoActivity.class);
        intent.putExtras(bundle);
        try{
            Activity activity = ActivityUtils.getTopActivity();
            if (activity != null) {
                activity.startActivity(intent);
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                IdCardApplication.getInstance().startActivity(intent);
            }
        }catch (Exception e){
            e.printStackTrace();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            IdCardApplication.getInstance().startActivity(intent);
        }
    }

    private String generateContextKey() {
        return ID_CARD + mAtomicIndex.getAndIncrement();
    }
}