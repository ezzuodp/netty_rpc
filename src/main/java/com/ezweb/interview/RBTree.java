package com.ezweb.interview;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * RedBlack Tree
 *
 * @author : zuodp
 * @version : 1.10
 */
public class RBTree<K, T extends RBTreeItem<K>> {

	private AtomicReference<RBTreeNode<T>> root;
	private RBTreeNode<T> sentinel;

	public RBTree() {
		this.sentinel = new RBTreeNode<>();
		rbt_black(this.sentinel);
		this.root = new AtomicReference<>(this.sentinel);
	}

	public void insert(T v) {
		RBTreeNode<T> node = new RBTreeNode<>(v);

		RBTreeNode<T> temp, sentinel;
		// 1.节点非红即黑。
		// 2.根节点是黑色。
		// 3.所有null结点称为叶子节点，且认为颜色为黑
		// 4.所有红节点的子节点都为黑色。
		// 5.从任一节点到其叶子节点的所有路径上都包含相同数目的黑节点。

		/* a binary tree insert */
		sentinel = this.sentinel;

		if (root.get() == this.sentinel) {
			node.parent = null;
			node.left = sentinel;
			node.right = sentinel;
			rbt_black(node); // insert root, set black.
			this.root.set(node);
			return;
		}

		default_rbt_insert_value(root.get(), node, sentinel);  // 新节点，直接设为 red.

		/* re-balance tree */
		while (node != root.get() && rbt_is_red(node.parent)) {   // 新节点&父节点都是red，就要进行 re-balance tree.

			if (node.parent == node.parent.parent.left) {
				temp = node.parent.parent.right;                  // 叔节点

				if (rbt_is_red(temp)) {                           // 叔节点是red
					rbt_black(node.parent);                       // 父节点,叔节点都设为 black.
					rbt_black(temp);
					rbt_red(node.parent.parent);                  // 祖父节点，设为red. 继续.
					node = node.parent.parent;

				} else {                                          // 叔节点是 black.
					if (node == node.parent.right) {
						node = node.parent;
						rbt_left_rotate(root, sentinel, node); // 左旋，祖节点作为父节点的右儿子，父节点的右儿子写到祖节点左儿子.
					}

					rbt_black(node.parent);                      // node是red, parent是black
					rbt_red(node.parent.parent);                 // parent.parent是red
					rbt_right_rotate(root, sentinel, node.parent.parent);
				}

			} else {
				temp = node.parent.parent.left;

				if (rbt_is_red(temp)) {
					rbt_black(node.parent);
					rbt_black(temp);
					rbt_red(node.parent.parent);
					node = node.parent.parent;

				} else {
					if (node == node.parent.left) {
						node = node.parent;
						rbt_right_rotate(root, sentinel, node);
					}

					rbt_black(node.parent);
					rbt_red(node.parent.parent);
					rbt_left_rotate(root, sentinel, node.parent.parent);
				}
			}
		}
		// root 设为 black
		rbt_black(root.get());
	}

	public Optional<T> min() {
		RBTreeNode<T> node = rbt_min_node(this.root.get(), this.sentinel);
		return node != null && node != this.sentinel ? Optional.of(node.val) : Optional.empty();
	}

	public Optional<T> delete(K k) {
		RBTreeNode<T> node = rbt_find_node_by_key(this.root.get(), this.sentinel, k);
		if (node == null) return null;

		boolean red = false;
		//AtomicReference<RBTreeNode<T>> root;
		RBTreeNode<T> sentinel, subst, temp, w;

		/* a binary tree delete */
		//root = new AtomicReference<>(this.root);
		sentinel = this.sentinel;

		if (node.left == sentinel) {
			temp = node.right;
			subst = node;

		} else if (node.right == sentinel) {
			temp = node.left;
			subst = node;

		} else {
			subst = rbt_min_node(node.right, sentinel);

			if (subst.left != sentinel) {
				temp = subst.left;
			} else {
				temp = subst.right;
			}
		}

		if (subst == root.get()) {
			root.set(temp);
			rbt_black(this.root.get());

			/* DEBUG stuff */
			node.left = null;
			node.right = null;
			node.parent = null;

			return Optional.of(node.val);
		}

		red = rbt_is_red(subst);

		if (subst == subst.parent.left) {
			subst.parent.left = temp;
		} else {
			subst.parent.right = temp;
		}

		if (subst == node) {
			temp.parent = subst.parent;
		} else {
			if (subst.parent == node) {
				temp.parent = subst;
			} else {
				temp.parent = subst.parent;
			}

			subst.left = node.left;
			subst.right = node.right;
			subst.parent = node.parent;
			rbt_copy_color(subst, node);

			if (node == root.get()) {
				root.set(subst);
				/*this.root = root.get();*/
			} else {
				if (node == node.parent.left) {
					node.parent.left = subst;
				} else {
					node.parent.right = subst;
				}
			}

			if (subst.left != sentinel) {
				subst.left.parent = subst;
			}

			if (subst.right != sentinel) {
				subst.right.parent = subst;
			}
		}

		/* DEBUG stuff */
		node.left = null;
		node.right = null;
		node.parent = null;

		if (red) {
			return Optional.of(node.val);
		}

		/* a delete fixup */
		while (temp != root.get() && rbt_is_black(temp)) {

			if (temp == temp.parent.left) {
				w = temp.parent.right;

				if (rbt_is_red(w)) {
					rbt_black(w);
					rbt_red(temp.parent);
					rbt_left_rotate(root, sentinel, temp.parent);
					w = temp.parent.right;
				}

				if (rbt_is_black(w.left) && rbt_is_black(w.right)) {
					rbt_red(w);
					temp = temp.parent;

				} else {
					if (rbt_is_black(w.right)) {
						rbt_black(w.left);
						rbt_red(w);
						rbt_right_rotate(root, sentinel, w);
						w = temp.parent.right;
					}

					rbt_copy_color(w, temp.parent);
					rbt_black(temp.parent);
					rbt_black(w.right);
					rbt_left_rotate(root, sentinel, temp.parent);
					temp = root.get();
				}

			} else {
				w = temp.parent.left;

				if (rbt_is_red(w)) {
					rbt_black(w);
					rbt_red(temp.parent);
					rbt_right_rotate(root, sentinel, temp.parent);
					w = temp.parent.left;
				}

				if (rbt_is_black(w.left) && rbt_is_black(w.right)) {
					rbt_red(w);
					temp = temp.parent;

				} else {
					if (rbt_is_black(w.left)) {
						rbt_black(w.right);
						rbt_red(w);
						rbt_left_rotate(root, sentinel, w);
						w = temp.parent.left;
					}

					rbt_copy_color(w, temp.parent);
					rbt_black(temp.parent);
					rbt_black(w.left);
					rbt_right_rotate(root, sentinel, temp.parent);
					temp = root.get();
				}
			}
		}

		rbt_black(temp);

		return Optional.of(node.val);
	}

	public Optional<T> find(K k) {
		RBTreeNode<T> node = rbt_find_node_by_key(this.root.get(), this.sentinel, k);
		if (node == null) return Optional.empty();
		return Optional.of(node.val);
	}

	private RBTreeNode<T> rbt_min_node(RBTreeNode<T> node, RBTreeNode<T> sentinel) {
		while (node != null && node != sentinel && node.left != sentinel) {
			node = node.left;
		}
		return node;
	}

	private RBTreeNode<T> rbt_find_node_by_key(RBTreeNode<T> node, RBTreeNode<T> sentinel, K key) {
		if (node == sentinel) return null;
		do {
			T tv = node.val;
			int r = tv.compareTo(key);
			if (r == 0) {
				return node;
			}
			node = r > 0 ? node.left : node.right;
		} while (node != sentinel);

		return null;
	}

	private void rbt_right_rotate(AtomicReference<RBTreeNode<T>> root, RBTreeNode<T> sentinel, RBTreeNode<T> node) {
		RBTreeNode<T> temp;

		temp = node.left;
		node.left = temp.right;

		if (temp.right != sentinel) {
			temp.right.parent = node;
		}

		temp.parent = node.parent;

		if (node == root.get()) {
			root.set(temp);

		} else if (node == node.parent.right) {
			node.parent.right = temp;

		} else {
			node.parent.left = temp;
		}

		temp.right = node;
		node.parent = temp;
	}

	private void rbt_left_rotate(AtomicReference<RBTreeNode<T>> root, RBTreeNode<T> sentinel, RBTreeNode<T> node) {
		RBTreeNode<T> temp;

		temp = node.right;
		node.right = temp.left;

		if (temp.left != sentinel) {
			temp.left.parent = node;
		}

		temp.parent = node.parent;

		if (node == root.get()) {
			root.set(temp);

		} else if (node == node.parent.left) {
			node.parent.left = temp;

		} else {
			node.parent.right = temp;
		}

		temp.left = node;
		node.parent = temp;
	}

	private void default_rbt_insert_value(RBTreeNode<T> begin, RBTreeNode<T> node, RBTreeNode<T> sentinel) {
		boolean left = false;
		for (; ; ) {
			left = node_cmp_proc(node, begin) < 0;
			RBTreeNode<T> p = (left ? begin.left : begin.right);
			if (p == sentinel) {
				break;
			}
			begin = p;
		}

		if (left)
			begin.left = node;
		else
			begin.right = node;

		node.parent = begin;
		node.left = sentinel;
		node.right = sentinel;
		rbt_red(node); // 新节点, 直接为red.
	}

	private int node_cmp_proc(RBTreeNode<T> a, RBTreeNode<T> b) {
		return a.val.compareTo(b.val.key());
	}

	private void rbt_black(RBTreeNode<T> node) {
		node.color = Color.BLACK;
	}

	private void rbt_red(RBTreeNode<T> node) {
		node.color = Color.RED;
	}

	private void rbt_copy_color(RBTreeNode<T> n1, RBTreeNode<T> n2) {
		n1.color = n2.color;
	}

	private boolean rbt_is_red(RBTreeNode<T> node) {
		return node.color == Color.RED;
	}

	private boolean rbt_is_black(RBTreeNode<T> node) {
		return node.color == Color.BLACK;
	}

	private static class RBTreeNode<T> {
		RBTreeNode<T> left = null;
		RBTreeNode<T> right = null;
		RBTreeNode<T> parent = null;
		Color color = Color.BLACK;
		T val = null;

		public RBTreeNode(T val) {
			this.val = val;
		}

		public RBTreeNode() {
		}
	}

	enum Color {
		RED,
		BLACK
	}
}

