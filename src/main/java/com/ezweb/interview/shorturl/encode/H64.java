package com.ezweb.interview.shorturl.encode;

import java.util.Arrays;

/**
 * @author : zuodp
 * @version : 1.10
 */
public abstract class H64 {

	public static Long hex2Long(String hex) {
		long v = 0;
		for (int i = 0; i < hex.length(); ++i) {
			char c = hex.charAt(i);
			int ci = Arrays.binarySearch(H64_cv, c);
			v = (v << h64_shift | ci);
		}
		return v;
	}

	public static String long2Hex(Long id) {
		char[] a = new char[11];
		int i = a.length;
		while (id > 0) {
			char f = H64_cv[(int) (id & h64)];
			a[--i] = f;
			id = id >>> h64_shift;
		}
		return new String(a, i, a.length - i);
	}

	private final static char[] H64_cv = "-0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz".toCharArray();
	private final static int h64 = 0b111111;
	private final static int h64_shift = 6;
}
