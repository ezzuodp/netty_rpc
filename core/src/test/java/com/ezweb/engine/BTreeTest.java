package com.ezweb.engine;

import com.ezweb.engine.util.BTree;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/11/12
 */
public class BTreeTest {
	@Test
	public void testBtree() {
		// btree 索引
		BTree<Integer, String> st = new BTree<>();
		for (int i = 0; i < 1024; ++i) {
			st.put(Integer.valueOf(i), "11_" + i);
		}
		System.out.println(st.size());
		System.out.println(st.height());
		Assert.assertEquals("11_0", st.get(Integer.valueOf(0)));
	}

}
