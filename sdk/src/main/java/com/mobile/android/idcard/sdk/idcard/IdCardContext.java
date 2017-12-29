package com.mobile.android.idcard.sdk.idcard;

import android.os.Bundle;

import com.mobile.android.idcard.sdk.handler.IHandler;

import java.util.HashMap;
import java.util.Map;

public class IdCardContext {

    public static Map<String, IdCardContext> mIdCardContextMap = new HashMap<>();

    public String mContextKey;

    private IIdCardManagerResponse mResponse;

    private IHandler mDeviceHandler;

    private IdCardInfo mIdCardInfo;

    public IdCardContext(String contextKey) {
        mContextKey = contextKey;
        mIdCardInfo = new IdCardInfo();
    }

    public void setResponse(IIdCardManagerResponse response) {
        mResponse = response;
    }

    public IHandler getDeviceHandler() {
        return mDeviceHandler;
    }

    public void setDeviceHandler(IHandler deviceHandler) {
        this.mDeviceHandler = deviceHandler;
    }

    public void setIdCardInfo(IdCardInfo idCardInfo) {
        mIdCardInfo = idCardInfo;
    }

    public static IdCardContext get(String contextKey) {
        IdCardContext idCardContext;
        if (mIdCardContextMap.containsKey(contextKey)) {
            idCardContext = mIdCardContextMap.get(contextKey);
        } else {
            idCardContext = new IdCardContext(contextKey);
            mIdCardContextMap.put(contextKey, idCardContext);
        }
        return idCardContext;
    }

    public void sendIdCard() {
        if (mResponse == null) {
            return;
        }
        if (mIdCardInfo != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(IdCardConstant.KEY_ID_CARD_DATA, mIdCardInfo);
            mResponse.onResult(bundle);
        } else {
            mResponse.onError(1, "未获取到二代身份证信息");
        }
    }
}
