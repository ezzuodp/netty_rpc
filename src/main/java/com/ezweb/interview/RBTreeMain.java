package com.ezweb.interview;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class RBTreeMain {
	public static void main(String[] args) {
		RBTree<Integer> tree = new RBTree<>();
		tree.init();
		for (int i = 1; i < 100; ++i) {
			tree.insert(i);
		}
		for (int i = 1; i < 100; ++i) {
			System.out.println("tree.afterDel = " + tree.min());
			tree.delete(i);
		}
	}
}
