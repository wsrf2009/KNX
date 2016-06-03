package com.sation.knxcontroller.knxdpt;

import java.math.BigDecimal;

public class DPT9 {

	public static byte[] getBytes(float f) {
		int Ms = (f>=0)?0:1;
		f = Math.abs(f);
		
		int E = 0;
		float M = 0;
		while(true) {
			M = (f/(int)Math.pow(2, E)) * 100;
			if(0 == Ms) {
				if((3 == E) && (2048 == M)) {
					M--;
					break;
				} else if (M <= 2048) {
					break;
				}
			} else if(1 == Ms) {
				M = 2048-M;
				if(M>0) {
					BigDecimal b = new BigDecimal(M);
					M = b.setScale(0, BigDecimal.ROUND_HALF_UP).floatValue();
					break;
				}
			}
			
			if(E>15) {
				break;
			}
			
			E++;
		}

		byte[] arr = new byte[2];
		arr[0] = (byte)((Ms << 7) | ((E&0x0F) << 3) | ((int)(M/256)&0x07));
		arr[1] = Integer.valueOf(Integer.toHexString((int)(M%256)), 16).byteValue();
 
		return arr;
	}
	
	public static float getFloat(byte[] arr) {
		if(2 != arr.length) {
			return .0f;
		}
		
		String h = Integer.toHexString(arr[0]);
		String l = Integer.toHexString(arr[1]);
		h = (h.length()>2)? h.substring(h.length()-2):h;
		l = (l.length()>2)?l.substring(l.length()-2):l;
		if((h.equals("7F")) && (l.equals("FF"))) {
			return 0.0f;
		}
		
		int Ms = (arr[0] >> 7) & 0x01;
		int E = (arr[0] >> 3) & 0x07;
		int M = (arr[0]&0x07)*256 + Integer.parseInt(l, 16);

		if(1 == Ms) {
			M = -2048 + M;
		}
		
		float f = (M * 0.01f) * (int)Math.pow(2, E);
		
		return f;
	}

}
