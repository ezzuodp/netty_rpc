package com.ezweb.engine;

import com.ezweb.engine.util.MaxHeap;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class MaxHeapTest {
	@Test
	public void maxHeap() {
		int[] array = new int[50];
		for (int i = 0; i < array.length; ++i)
			array[i] = i + 1;

		MaxHeap<Integer> maxHeap = new IntegerMaxHeap(100);
		for (int i = 0; i < array.length; ++i)
			maxHeap.push(array[i]);

		for (int i = array.length - 1; i >= 0; --i) {
			int v = maxHeap.pop();
			Assert.assertEquals(v, array[i]);
			Assert.assertEquals(i, maxHeap.size());
		}
	}

	private static class IntegerMaxHeap extends MaxHeap<Integer> {
		IntegerMaxHeap(int size) {
			super(size);
		}
	}
}
