package com.mobile.android.idcard.sdk.idcard;

public interface IIdCardManagerCallback<V> {
    void run(IdCardFuture<V> future);
}
