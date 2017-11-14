package com.sation.knxcontroller.knxdpt;

import com.sation.knxcontroller.util.Log;

public class DPT14 {
	private static final String TAG = "DPT14";
	
	public static byte[] float2bytes(float f) {
		
		// 把float转换为byte[]
		int fbit = Float.floatToIntBits(f);

		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (fbit >> (24 - i * 8));
		}

		// 翻转数组
		int len = b.length;
		// 建立一个与源数组元素类型相同的数组
		byte[] dest = new byte[len];
		// 为了防止修改源数组，将源数组拷贝一份副本
		System.arraycopy(b, 0, dest, 0, len);
		byte temp;
		// 将顺位第i个与倒数第i个交换
		for (int i = 0; i < len / 2; ++i) {
			temp = dest[i];
			dest[i] = dest[len - i - 1];
			dest[len - i - 1] = temp;
		}
//		
//		for(int i=0; i<dest.length; i++) {
//			Log.i(TAG, "dest["+i+"]" +"="+ Integer.toHexString(dest[i]&0xFF));
//		}

		return dest;
	}
	
	public static float bytes2float(byte[] arr) {
//		if(4 != arr.length) {
//			return .0f;
//		}
		
		int l;
		l = arr[3];
		l &= 0xff;
		l |= ((long) arr[2] << 8);
		l &= 0xffff;
		l |= ((long) arr[1] << 16); 
		l &= 0xffffff;
		l |= ((long) arr[0] << 24);
		
		return Float.intBitsToFloat(l);
	}
}
