package com.ezweb.engine.util;

/**
 * B-Tree树
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/11/12
 */
public class BTree<Key extends Comparable<Key>, Value> {
	static final int NODE_MAX_CHILD = 16;      // max children per B-tree node = M-1

	private Node<Key, Value> root;             // root of the B-tree
	private int treeHeight;                    // height of the B-tree
	private int kvNum;                         // number of key-value pairs in the B-tree

	private static final class Node<Key, Value> {
		@SuppressWarnings("unchecked")
		private Entry<Key, Value>[] children = new Entry[NODE_MAX_CHILD];   // the array of children
		// number of children
		private int childNum;

		private Node(int k) {
			childNum = k;
		}
	}

	private static class Entry<Key, Value> {
		Key key;
		Value value;
		Node<Key, Value> next;
	}

	public BTree() {
		root = new Node<>(0);
	}

	public int size() {
		// return number of key-value pairs in the B-tree
		return kvNum;
	}

	public int height() {
		return treeHeight;
	}

	public void put(Key key, Value value) {
		Node<Key, Value> u = insert(root, key, value, treeHeight);
		kvNum++;

		if (u == null) return;

		// need to split root
		Node<Key, Value> t = new Node<>(2);
		t.children[0] = newLinkEntry(root.children[0].key, root);
		t.children[1] = newLinkEntry(u.children[0].key, u);
		root = t;
		treeHeight++;
	}

	public Value get(Key key) {
		return search(root, key, treeHeight);
	}

	private Value search(Node<Key, Value> x, Key key, int ht) {
		Entry<Key, Value>[] children = x.children;
		// external node
		if (ht == 0) {
			for (int j = 0; j < x.childNum; j++) {
				if (eq(key, children[j].key)) return children[j].value;
			}
		}
		// internal node
		else {
			for (int j = 0; j < x.childNum; j++) {
				if (j + 1 == x.childNum || less(key, children[j + 1].key))
					return search(children[j].next, key, ht - 1);
			}
		}
		return null;
	}

	private Node<Key, Value> insert(Node<Key, Value> h, Key key, Value value, int ht) {
		int j;
		Entry<Key, Value> t = newDataEntry(key, value);
		// external node
		if (ht == 0) {
			for (j = 0; j < h.childNum; j++) {
				if (less(key, h.children[j].key)) // 找到放 t 的位置
					break;
			}
		}
		// internal node
		else {
			for (j = 0; j < h.childNum; j++) {
				if ((j + 1 == h.childNum) || less(key, h.children[j + 1].key)) {
					Node<Key, Value> u = insert(h.children[j++].next, key, value, ht - 1);
					if (u == null) return null;
					t.key = u.children[0].key;
					t.next = u;
					break;
				}
			}
		}
		//noinspection ManualArrayCopy
		for (int i = h.childNum; i > j; i--) { // h[3] 后移到 h[4] 中.
			h.children[i] = h.children[i - 1];
		}
		h.children[j] = t;
		h.childNum++;

		if (h.childNum < NODE_MAX_CHILD)
			return null;
		else
			return split(h);
	}

	private Node<Key, Value> split(Node<Key, Value> h) {
		Node<Key, Value> t = new Node<>(NODE_MAX_CHILD / 2);
		h.childNum = NODE_MAX_CHILD / 2;

		// 将 h.childNum 分一半到 t.
		for (int j = 0; j < NODE_MAX_CHILD / 2; j++) {
			t.children[j] = h.children[NODE_MAX_CHILD / 2 + j];
			h.children[NODE_MAX_CHILD / 2 + j] = null;
		}

		return t;
	}

	// for debugging
	public String toString() {
		return "BTree[H:" + this.treeHeight + ",S:" + this.kvNum + "]@" + Integer.toHexString(this.hashCode());
	}

	private Entry<Key, Value> newLinkEntry(Key key, Node<Key, Value> next) {
		Entry<Key, Value> e = new Entry<>();
		e.key = key;
		e.next = next;
		return e;
	}

	private Entry<Key, Value> newDataEntry(Key key, Value value) {
		Entry<Key, Value> e = new Entry<>();
		e.key = key;
		e.value = value;
		return e;
	}

	// comparison functions - make Comparable instead of Key to avoid casts
	private boolean less(Key k1, Key k2) {
		return k1.compareTo(k2) < 0;
	}

	private boolean eq(Key k1, Key k2) {
		return k1.compareTo(k2) == 0;
	}
}
