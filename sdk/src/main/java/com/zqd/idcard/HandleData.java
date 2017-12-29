package com.zqd.idcard;


public class HandleData {

	/** 
	* 将两个ASCII字符合成一个字节； 
	* 如："EF"--> 0xEF 
	* @param src0 byte 
	* @param src1 byte 
	* @return byte 
	*/ 
	public static byte uniteBytes(byte src0, byte src1) { 
		byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
		_b0 = (byte)(_b0 << 4); 
		byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
		byte ret = (byte)(_b0 ^ _b1); 
		return ret; 
	} 

	/** 
	* 将指定字符串src，以每两个字符分割转换为16进制形式 
	* 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF, 0xD9} 
	* @param src String 
	* @return byte[] 
	*/ 
	public static byte[] HexString2Bytes(String src){
		byte[] ret = new byte[src.length()/2]; 
		byte[] tmp = src.getBytes(); 
		for(int i=0; i<src.length()/2; i++){ 
			ret[i] = uniteBytes(tmp[i*2], tmp[i*2+1]); 
		} 
		return ret; 
	} 
	
    /**32位int转byte[]*/  
    public static byte[] int2byte(int res) {  
        byte[] targets = new byte[4];  
        targets[0] = (byte) (res & 0xff);// 最低位  
        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位  
        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位  
        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。  
        return targets;  
    }  
    /**32位int转byte[]*/  
    public static byte[] int24byte(int res) {  
        byte[] targets = new byte[4];  
        targets[3] = (byte) (res & 0xff);// 最低位  
        targets[2] = (byte) ((res >> 8) & 0xff);// 次低位  
        targets[1] = (byte) ((res >> 16) & 0xff);// 次高位  
        targets[0] = (byte) (res >>> 24);// 最高位,无符号右移。  
        return targets;  
    } 
  
    /**32位int转byte[]*/  
    public static byte[] int22byte(int res) {  
        byte[] targets = new byte[2];  
        targets[1] = (byte) (res & 0xff);// 最低位  
        targets[0] = (byte) ((res >> 8) & 0xff);// 次低位  
        return targets;  
    }  
    
    
    /** 
     * 将长度为2的byte数组转换为int 
     *  
     * @param res 
     *            byte[] 
     * @return int 
     * */  
    public static int twobytetoint(byte[] res) {  
        // res = InversionByte(res);  
        // 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000  
        int targets = (res[1] & 0xff) | ((res[0] << 8) & 0xff00); // | 表示安位或  
        return targets;  
    }  
    
    
    
    /** 
     * 将长度为2的byte数组转换为16位int 
     *  
     * @param res 
     *            byte[] 
     * @return int 
     * */  
    public static int byte2int(byte[] res) {  
        // res = InversionByte(res);  
        // 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000  
        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00); // | 表示安位或  
        return targets;  
    }  
	/**
	 * 对十六进制byte数组取反
	 * @param buff 16进制数组
	 * @return 16进制数组
	 */
	public static byte[] backByte(byte[] buff){
		StringBuffer sb = new StringBuffer();
		  for (int i=0;i<buff.length;i++){
			  
		      int b = ~buff[i];
		      String str = Integer.toHexString(b);
		      //Log.e("str = ", str+"\n");
		      if(str.length()>2){
		    	  str = str.substring(str.length()-2,str.length());
		      }
		      else if(str.length()<2){
		    	  str = "0"+str;
		      }
		      sb.append(str);
		  }
		  return HexString2Bytes(sb.toString());
		 } 
	/**
	 * 字符序列转换为16进制字符串
	 * 
	 * @param src
	 * @return
	 */
	public static String bytesToHexString1(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		char[] buffer = new char[2];
		for (int i = 0; i < src.length; i++) {
			buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
			buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
			stringBuilder.append(buffer);
		}
		return stringBuilder.toString().toUpperCase();
	}
	
	/**
	 * 字符序列转换为16进制字符串
	 * 
	 * @param src
	 * @return
	 */
	public static int[] bytesToHexByte(byte[] src) {
		
		int[] b = new int[src.length];
		if (src == null || src.length <= 0) {
			return null;
		}
		char[] buffer = new char[2];
		for (int i = 0; i < src.length; i++) {
			StringBuilder stringBuilder = new StringBuilder();
			buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
			buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
			stringBuilder.append(buffer);
			b[i] = Integer.parseInt(stringBuilder.toString());
		}
		return b;
	}

	/**
	 * java二进制,字节数组,字符,十六进制,BCD编码转换2007-06-07 00:17 把16进制字符串转换成字节数组
	 * 
	 * @param hex
	 * @return
	 */
	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}
}
