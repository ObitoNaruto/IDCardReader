package com.example.imitatesdk;

public class ConsantHelper {
	public static final String VERSION = "V1.0";
	public static final int READ_CARD_SUCCESS = 100; // 读取成功
	
	public static final int READ_CARD_second = 2; // 中间异常读卡失败第二次读卡
	
	public static final int NFC_CONNECT_ERR = -25; // 调用NFC.connect()返回false
	public static final int NFC_CARD_TYPE_ERR = -24; // NFC Type 不支持
	public static final int NFC_TAG_ERR = -24; // NFC Tag 为null或者异常
	public static final int NFC_CARD_ERR = -23; // NFC读卡异常
	
	public static final int NET_CONNECT_SERVER_ERR = -30; // socket无法连接服务器
	public static final int NET_SEND_ERR = -31; // 向服务器发送数据错误
	public static final int NET_RECIVE_ERR = -32; // 从服务器接收数据错误
	
	
	public static final int Progress  = 82;//进度和服务器建立连接
	public static final int ProgressPass  = 86;//身份证认证通过
	
	
	public static final int READ_CARD_NO_READ = -1; 
	public static final int READ_CARD_BUSY = -2; // 
	public static final int READ_CARD_NET_ERR = 3; // 网络异常，请检查当前状况
	public static final int READ_CARD_NO_CARD = -4; // 请检 查证件 查证件 是否放置在设备上
													// 是否放置在设备上
	public static final int READ_CARD_SAM_ERR = -5; // 服务器处理异常 服务器处理异常 服务器处理异常
	public static final int READ_CARD_OTHER_ERR = -6; 
	public static final int READ_CARD_NEED_TRY = -7; // 出现错误需要重试 出现错误需要重试
														// 出现错误需要重试
	public static final int READ_CARD_OPEN_FAILED = -8; // 打开身份证错误 打开身份证错误
														// 打开身份证错误
	public static final int READ_CARD_NO_CONNECT = -9; // 无法连接服务器 无法连接服务器
														// 无法连接服务器
	public static final int READ_CARD_NO_SERVER = -10; // 服务器连接超时 服务器连接超时
														// 服务器连接超时
	public static final int READ_CARD_FAILED = -11;; // 服务器连接失败 服务器连接失败 服务器连接失败
	public static final int READ_CARD_SN_ERR = -12; // 服务器繁处理忙 服务器繁处理忙 服

}