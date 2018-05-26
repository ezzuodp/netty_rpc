package com.ezweb.interview;

import com.ezweb.engine.util.MaxHeap;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * 数组排序算法(插入|堆|归并)
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
	static void insertSort(int[] array, int len) {
		for (int j = 1; j < len; ++j) {
			int tmp = array[j];
			int i = 0;
			for (i = j; i > 0; --i) {
				if (array[i - 1] > tmp) {       // 写错了，一定要array[i-1]
					array[i] = array[i - 1];    // 写错了，一定要array[i]=array[i-1].
				} else {
					break;
				}
			}
			array[i] = tmp;
		}
	}

	/**
	 * heap sort
	 */
	static void heapSort(int[] array, int len) {
		MaxHeap<Integer> heap = new MaxHeap<Integer>(len) {
		};
		for (int i = 0; i < len; ++i) {
			heap.push(array[i]);
		}
		for (int i = len - 1; i >= 0; --i) {
			int v = heap.pop();
			array[i] = v;
		}
	}

	/**
	 * 归并排序
	 */
	static void mergeSort(int[] elements, int len) {
		int[] aux = new int[len];
		int start = 0, end = len - 1;
		doMergeSort(elements, start, end, aux);
	}

	static void doMergeSort(int[] elements, int start, int end, int[] aux) {
		if (start >= end) {
			return;
		}
		int mid = (start + end) / 2;
		doMergeSort(elements, start, mid, aux);
		doMergeSort(elements, mid + 1, end, aux);
		merge(elements, start, mid, end, aux);
	}

	static void merge(int[] elements, int start, int mid, int end, int[] aux) {
		int lb = start, rb = mid + 1, auxIndex = start;
		while (lb <= mid && rb <= end) {
			if (Integer.compare(elements[lb], elements[rb]) <= 0) {
				aux[auxIndex++] = elements[lb++];
			} else {
				aux[auxIndex++] = elements[rb++];
			}
		}

		if (lb < mid + 1) {
			while (lb <= mid) {
				aux[auxIndex++] = elements[lb++];
			}
		} else {
			while (rb <= end) {
				aux[auxIndex++] = elements[rb++];
			}
		}

		for (int i = start; i <= end; i++) {
			elements[i] = aux[i];
		}
	}

	static void randomBytes(int[] array, @SuppressWarnings("unused") int len) {
		SecureRandom sr = new SecureRandom();
		Arrays.setAll(array, (int operand) -> Math.abs(sr.nextInt(1000)));
	}

	public static void main(String[] args) {
		int[] array = new int[128];

		randomBytes(array, 128);
		insertSort(array, 128);
		System.out.println(Arrays.toString(array));
		find(array, 128, array[123]);
		find(array, 128, array[127]);

		randomBytes(array, 128);
		heapSort(array, 128);
		System.out.println(Arrays.toString(array));
		find(array, 128, array[123]);
		find(array, 128, array[127]);

		randomBytes(array, 128);
		mergeSort(array, 128);
		System.out.println(Arrays.toString(array));
		find(array, 128, array[123]);
		find(array, 128, array[127]);
	}
}
