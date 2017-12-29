package com.mobile.android.idcard.sdk.handler;


import android.nfc.Tag;
import android.nfc.tech.NfcB;
import android.util.Log;

import java.io.IOException;

public class NfcBHandler implements IHandler<NfcB> {

    private static final String TAG = NfcBHandler.class.getSimpleName();

    private Tag mTag;
    private NfcB mNfcB;

    public NfcBHandler(Tag tag) {
        mTag = tag;
    }

    @Override
    public void init() {
        if (mTag == null) {
            return;
        }
        mNfcB = NfcB.get(mTag);
    }

    @Override
    public NfcB get() {
        return mNfcB;
    }

    @Override
    public void connect(){
        try{
            mNfcB.connect();
        }catch (IOException e) {
            Log.e(TAG, "NfcHandler connect failed.", e);
        }
    }

    @Override
    public boolean isConnected() {
        return mNfcB.isConnected();
    }

    @Override
    public byte[] transceive(byte[] data) {
        try{
            return mNfcB.transceive(data);
        }catch (IOException e) {
            Log.e(TAG, "NfcHandler transceive failed.", e);
            return null;
        }
    }

    @Override
    public void close() {
        try{
            mNfcB.close();
        }catch (IOException e) {
            Log.e(TAG, "NfcHandler close failed.", e);
        }
    }
}
