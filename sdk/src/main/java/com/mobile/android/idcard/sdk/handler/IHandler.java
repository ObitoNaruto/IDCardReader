package com.mobile.android.idcard.sdk.handler;


public interface IHandler<T> {

    void init();

    <T extends Object> T get();

    void connect();

    boolean isConnected();

    byte[] transceive(byte[] data);

    void close();
}
