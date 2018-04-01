package com.ezweb.interview;

/**
 * RBTreeItem 对象
 *
 * @author : zuodp
 * @version : 1.10
 */
public interface RBTreeItem<K> extends Comparable<K> {
	/**
	 * 对象KEY
	 */
	K key();

	/**
	 * 比较对象KEY
	 */
	@Override
	int compareTo(K otherKey);
}
