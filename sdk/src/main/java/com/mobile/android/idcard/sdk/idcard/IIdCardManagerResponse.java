package com.mobile.android.idcard.sdk.idcard;


import android.os.Bundle;

public interface IIdCardManagerResponse {

    void onResult(Bundle bundle);

    void onError(int errorCode, String errorMsg);
}
