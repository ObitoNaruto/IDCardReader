package com.mobile.android.idcard.sdk.idcard;

import android.os.Bundle;

import com.mobile.android.idcard.sdk.handler.IHandler;

public interface IIdCardService {
    void readIdCard(IIdCardManagerResponse response, Bundle bundle, IHandler deviceHandler);
}
