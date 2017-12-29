package com.zqd.idcard;


import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;

/**
 * 处理不同的卡片类型
 * 
 * @author 
 * 
 */
public class CardManager {

	public static String mTechLists[][];
	public static IntentFilter[] intentFiltersArray;

	static {
		try {
			// 做一个tech-list。可以看到是二维数据，每一个一维数组之间的关系是或，但是一个一维数组之内的各个项就是与的关系了
			mTechLists = new String[][] {
					new String[] { NfcF.class.getName() },
					new String[] { NfcA.class.getName() },
					new String[] { NfcB.class.getName() },
					new String[] { NfcV.class.getName() },
					new String[] { IsoDep.class.getName() },
					new String[] { MifareClassic.class.getName() } };
			
			IntentFilter ndef = new IntentFilter(
					NfcAdapter.ACTION_NDEF_DISCOVERED);
			IntentFilter techIntent = new IntentFilter(
					NfcAdapter.ACTION_TECH_DISCOVERED, "*/*");
			IntentFilter tagIntent = new IntentFilter(
					NfcAdapter.ACTION_TAG_DISCOVERED);
			intentFiltersArray = new IntentFilter[] { ndef, techIntent,
					tagIntent };
		} catch (MalformedMimeTypeException e) {
			e.printStackTrace();
		}

	}

}
