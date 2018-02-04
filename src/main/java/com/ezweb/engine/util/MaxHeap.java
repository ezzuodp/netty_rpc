package com.ezweb.engine.util;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 大根堆的Java实现
 *
 * @author : zuodp
 * @version : 1.10
 */
public abstract class MaxHeap<T extends Comparable<T>> {
	private int n;
	private int a;
	private T[] array;

	public MaxHeap(int size) {
		Type f = this.getClass().getGenericSuperclass();
		Type pv = ((ParameterizedType) f).getRawType();                // MaxHeap.class
		Type av = ((ParameterizedType) f).getActualTypeArguments()[0]; // T.class
		if (av instanceof Class) {
			// T.class
			Class<T> xx = (Class<T>) av;
			array = (T[]) Array.newInstance(xx, size);
			a = size;
			n = 0;
		}
	}

	public void push(T nv) {
		int hold_index = this.n++;                  // 写错了[++this.n]
		int parent_index = (hold_index - 1) / 2;
		while (hold_index > 0 && this.array[parent_index].compareTo(nv) < 0) { // 注意判断条件
			this.array[hold_index] = this.array[parent_index];
			hold_index = parent_index;
			parent_index = (hold_index - 1) / 2;
		}
		this.array[hold_index] = nv;
	}

	public T pop() {
		T v = this.array[0];
		int hold_index = 0;
		int min_child = (hold_index + 1) * 2;
		while (min_child <= this.n) {         // 写错了[hold_index >= this.n]，同时注意判断条件
			min_child -= ((min_child == this.n) || (this.array[min_child - 1].compareTo(this.array[min_child]) > 0)) ? 1 : 0;
			this.array[hold_index] = this.array[min_child];
			hold_index = min_child;
			min_child = (hold_index + 1) * 2;
		}
		this.array[hold_index] = this.array[--this.n];
		this.array[this.n] = null;
		return v;
	}

	public int size() {
		return n;
	}
}
