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
	public static int kmp(String str, String sub, int[] subNext, int index) {
		if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
			throw new IllegalArgumentException("str或者sub不能为空");
		}

		int j = 0;
		for (int i = index; i < str.length(); i++) {
			while (j > 0 && str.charAt(i) != sub.charAt(j)) {
				j = subNext[j - 1];
			}

			if (str.charAt(i) == sub.charAt(j)) {
				j++;
			}

			if (sub.length() == j) {
				return i - j + 1;
			}
		}

		return -1;
	}

	/**
	 * 用于生成部分匹配表
	 * ABCDABD
	 * 0000120
	 * 　－　"A"的前缀和后缀都为空集，共有元素的长度为0；
	 * <p>
	 * 　　－　"AB"的前缀为[A]，后缀为[B]，共有元素的长度为0；
	 * <p>
	 * 　　－　"ABC"的前缀为[A, AB]，后缀为[BC, C]，共有元素的长度0；
	 * <p>
	 * 　　－　"ABCD"的前缀为[A, AB, ABC]，后缀为[BCD, CD, D]，共有元素的长度为0；
	 * <p>
	 * 　　－　"ABCDA"的前缀为[A, AB, ABC, ABCD]，后缀为[BCDA, CDA, DA, A]，共有元素为"A"，长度为1；
	 * <p>
	 * 　　－　"ABCDAB"的前缀为[A, AB, ABC, ABCD, ABCDA]，后缀为[BCDAB, CDAB, DAB, AB, B]，共有元素为"AB"，长度为2；
	 * <p>
	 * 　　－　"ABCDABD"的前缀为[A, AB, ABC, ABCD, ABCDA, ABCDAB]，后缀为[BCDABD, CDABD, DABD, ABD, BD, D]，共有元素的长度为0。
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
		String v = "BBCABCDABABCDABCDABDEABCDABD";
		String f = "ABCDABD";

		int r = kmp(v, f, next(f), 0);
		System.out.println("r = " + r);
		r = kmp(v, f, next(f), r + f.length());
		System.out.println("r = " + r);

	}
}
