package com.ezweb.interview.shorturl.encode;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class Hex {
	private static final char[] HEX = "0123456789ABCDEF".toCharArray();

	public static String fromByteArray(byte[] v) {
		StringBuilder sb = new StringBuilder(64);
		for (int i = 0; i < v.length; ++i) {
			sb.append((char) (v[i] & 0xff));
		}
		return sb.toString();
	}
}
