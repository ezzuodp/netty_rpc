package com.ezweb.interview;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * 数组排序算法
 *
 * @author : zuodp
 * @version : 1.10
 */
public class ArraySort {
	// 出现次数超过一半的情况
	static void wxcount(int[] array, int len) {
		int count = 0;
		int result = 0;
		for (int i = 0; i < len; ++i) {
			if (count == 0) {
				result = array[i];
				count = 1;
			} else if (array[i] == result) {
				count++;
			} else {
				count--;
			}
		}
		if (count >= 1) {
			count = (len - count) / 2 + count - 1;
			System.out.println(String.format("Find val:%d cur:%d", result, count));
		}
	}

	// 2分查找
	static void find(int[] array, int len, int v) {
		int l = 0;
		int r = len - 1;
		int fc = 0;
		while (l <= r) {
			int m = l + (r - l) / 2;
			++fc;
			if (array[m] == v) {
				// 向前向后查可能相同
				int i = m - 1, j = m + 1;
				while (i >= 0 && array[i] == v) {
					--i;
				}
				while (j <= r && array[j] == v) {
					++j;
				}
				System.out.println(String.format("%d at array [%d, %d] find result: %d.", fc, i + 1, j - 1, v));
				break;
			} else if (array[m] < v) {
				l = m + 1;
			} else {
				r = m - 1;
			}
			System.out.println(String.format("%d at array find [%d] ... ", fc, v));
		}
	}

	/**
	 * insert sort
	 */
	static void sort(int[] array, int len) {
		int right = len - 1;             // right 必须是len-1
		for (int i = 0, j = i; i < right; j = ++i) {
			int tmp = array[i + 1];       // 直接取下一个数组值.

			while (array[j] > tmp) {
				array[j + 1] = array[j]; // array[j]后移到a[j+1]
				--j;
				if (j < 0) {             // **注意必须是到-1.
					break;
				}
			}
			array[j + 1] = tmp;
		}
	}

	/**
	 * 将数组中[基数在前，偶数在后]
	 */
	static void move2End(int[] array, int len) {
		int sj = len - 1;
		for (int i = 0; i < sj; ++i) {
			if (array[i] % 2 == 0) {
				int t = array[i];
				for (int j = sj; j > i; --j) {
					if (array[j] % 2 == 0) {

					} else {
						array[i] = array[j];
						array[j] = t;
						sj = j - 1;
						break;
					}
				}
			}
		}
	}

	static void random(int[] array, @SuppressWarnings("unused") int len) {
		SecureRandom sr = new SecureRandom();
		Arrays.setAll(array, (int operand) -> Math.abs(sr.nextInt(1000)));
	}

	public static void main(String[] args) {
		int[] array = new int[128];
		random(array, 128);
		System.out.println(Arrays.toString(array));
		sort(array, 128);
		System.out.println(Arrays.toString(array));
		find(array, 128, array[127]);
		move2End(array, 128);
		sort(array, 128);
		find(array, 128, array[127]);

		wxcount(new int[]{1, 4, 4, 4, 6, 4, 4, 9, 9}, 9);
	}
}
