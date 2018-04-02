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
			sb.append(HEX[(v[i] >> 4) & 0x0f]);
			sb.append(HEX[v[i] & 0x0f]);
		}
		return sb.toString();
	}

	public static byte[] toByteArray(String hex) {
		byte[] result = new byte[hex.length() / 2];
		for (int i = 0, j = 0; i < hex.length(); ++j) {
			int v1 = hex.charAt(i++) & 0x0f;
			int v2 = hex.charAt(i++) & 0x0f;
			int v = v1 << 4 | v2;
			result[j] = (byte) v;
		}
		return result;
	}
}
