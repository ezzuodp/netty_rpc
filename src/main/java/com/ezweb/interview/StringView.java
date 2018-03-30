package com.ezweb.interview;

/**
 * <一句话说明功能>
 * <功能详细描述>
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/3/30
 */
public class StringView {
	/**
	 * 用于计算匹配的位置（从头到尾）
	 */
	public static int kmp(String str, String sub) {
		if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
			throw new IllegalArgumentException("str或者sub不能为空");
		}

		int j = 0;
		int[] n = next(sub);
		for (int i = 0; i < str.length(); i++) {
			while (j > 0 && str.charAt(i) != sub.charAt(j)) {
				j = n[j - 1];
			}

			if (str.charAt(i) == sub.charAt(j)) {
				j++;
			}

			if (sub.length() == j) {
				int index = i - j + 1;
				return index;
			}
		}

		return -1;
	}

	/**
	 * 用于生成部分匹配表
	 */
	private static int[] next(String sub) {
		int[] n = new int[sub.length()];
		int x = 0;
		for (int i = 1; i < sub.length(); i++) {
			while (x > 0 && sub.charAt(x) != sub.charAt(x)) {
				x = n[x - 1];
			}

			if (sub.charAt(i) == sub.charAt(x)) {
				x++;
			}

			n[i] = x;
		}
		return n;
	}

	public static void main(String[] args) {
		String v = "BBCABCDABABCDABCDABDE";
		String f = "ABCDABD";

		int r = kmp(v, f);
		System.out.println("r = " + r);

	}
}
