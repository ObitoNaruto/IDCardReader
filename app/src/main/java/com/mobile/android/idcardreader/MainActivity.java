package com.mobile.android.idcardreader;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.imitatesdk.CardInfoActivity;
import com.mobile.android.idcard.sdk.handler.IHandler;
import com.mobile.android.idcard.sdk.handler.NfcBHandler;
import com.mobile.android.idcard.sdk.idcard.IIdCardManagerCallback;
import com.mobile.android.idcard.sdk.idcard.IdCardConstant;
import com.mobile.android.idcard.sdk.idcard.IdCardFuture;
import com.mobile.android.idcard.sdk.idcard.IdCardInfo;
import com.mobile.android.idcard.sdk.idcard.IdCardManager;
import com.zqd.idcard.CardManager;

public class MainActivity extends AppCompatActivity {

    // 关于NFC
    public NfcAdapter nfcAdapter;
    private PendingIntent mPendingIntent;
    private boolean isFirsRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 获取默认的NFC控制器
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            return;
        }
        if (!nfcAdapter.isEnabled()) {
            return;
        }
        findViewById(R.id.tv_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    readIdCard();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            setForground();
        }catch(Exception e){
//            Toast.makeText(cardinfo, "非NFC手机", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 设定前台系统
     */
    private void setForground() {
        // 设置优先级-前台发布系统，
        if (isFirsRun) {
            mPendingIntent = PendingIntent.getActivity(this, 0, getIntent(), 0);
            enableNdefExchangeMode(mPendingIntent);
            isFirsRun = false;
            enableReaderMode();
        }
    }

    /**
     * 设定前台系统可用
     *
     * @param mPendingIntent
     */
    private void enableNdefExchangeMode(PendingIntent mPendingIntent) {
        nfcAdapter.enableForegroundDispatch(this, mPendingIntent,
                CardManager.intentFiltersArray, CardManager.mTechLists);
    }

    @TargetApi(19)
    private void enableReaderMode() {
        Bundle options = new Bundle();
        options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 1000);
        int READER_FLAGS =  NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
        if (nfcAdapter != null) {
            nfcAdapter.enableReaderMode(this, new MyReaderCallback(), READER_FLAGS, options);
        }
    }

    @TargetApi(19)
    public class MyReaderCallback implements NfcAdapter.ReaderCallback {

        @Override
        public void onTagDiscovered(final Tag arg0) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    IHandler deviceHandler = new NfcBHandler(arg0);
                    deviceHandler.init();
//                    readIdCard();
                    Bundle bundle = new Bundle();
                    IdCardManager.getsInstance(MainActivity.this.getApplicationContext()).readIdCard(bundle, null, new IIdCardManagerCallback<Bundle>() {
                        @Override
                        public void run(IdCardFuture<Bundle> future) {
                            try{
                                Bundle extras = future.getResult();
                                if(extras != null) {
                                    IdCardInfo idCardInfo = extras.getParcelable(IdCardConstant.KEY_ID_CARD_DATA);
                                    Log.d("IdCard", (idCardInfo == null ? "null" : idCardInfo.toString()));
                                    Toast.makeText(MainActivity.this, idCardInfo.toString(), Toast.LENGTH_SHORT).show();
                                }

                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, null, deviceHandler);
//                    app.tag =arg0;
//
//                    zqdreadId.ReadBCard(host, port, 5000, null);
                }
            });
        }
    }

    //读取二代身份证
    private void readIdCard() {
        Bundle bundle = new Bundle();
        IdCardManager.getsInstance(this.getApplicationContext()).readIdCard(bundle, this, new IIdCardManagerCallback<Bundle>() {
            @Override
            public void run(IdCardFuture<Bundle> future) {
                try{
                    Bundle extras = future.getResult();
                    if(extras != null) {
                        IdCardInfo idCardInfo = extras.getParcelable(IdCardConstant.KEY_ID_CARD_DATA);
                        Log.d("IdCard", (idCardInfo == null ? "null" : idCardInfo.toString()));
                        Toast.makeText(MainActivity.this, idCardInfo.toString(), Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, null, null);
    }
}
