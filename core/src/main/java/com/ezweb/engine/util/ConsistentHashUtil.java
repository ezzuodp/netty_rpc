package com.ezweb.engine.util;

import com.google.common.hash.Hashing;
import io.netty.util.internal.StringUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性hash测试实现
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/11
 */
public class ConsistentHashUtil {
	private static final int num = 160;
	private TreeMap<Integer, Node> cluster_nodes = new TreeMap<>();
	private HashMap<String, Node> nodes = new HashMap<>();

	public static class Node {
		private final String id;
		private final int weight;

		public Node(String id, int weight) {
			this.id = id;
			this.weight = Math.max(weight, 1);
		}

		public String getId() {
			return id;
		}

		public int getWeight() {
			return weight;
		}

		@Override
		public String toString() {
			return String.format("Node{id:%s, w:%d}", id, weight);
		}
	}

	public void addNode(Node node) {
		nodes.put(node.id, node);
		// 每个  node 生成虚拟节点 * 64
		int vsize = node.weight * num;
		for (int j = 0; j < vsize; ++j) {
			String key = node.id + ":" + j;
			int k = Hashing.murmur3_32().newHasher().putUnencodedChars(key).hash().asInt();
			k = k & Integer.MAX_VALUE;
			cluster_nodes.put(k, node);
		}
	}

	public Node getNode(String key) {
		int k = Hashing.murmur3_32().newHasher().putUnencodedChars(key).hash().asInt();
		return getNode(k);
	}

	public Node getNode(int k) {
		k = k & Integer.MAX_VALUE;
		if (cluster_nodes.get(k) == null) {
			Map.Entry<Integer, Node> nextNode = cluster_nodes.ceilingEntry(k);
			if (nextNode == null) {
				nextNode = cluster_nodes.firstEntry();
			}
			return nextNode.getValue();
		}
		return cluster_nodes.get(k);
	}

	private static String padingLeft(String a, int size) {
		if (a.length() < size) {
			a = "00000000" + a;
			return a.substring(a.length() - size);
		} else if (a.length() == size)
			return a;
		else
			return a.substring(a.length() - size);
	}

	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer(20);

		byte[] bytes = new byte[16];
		new java.util.Random().nextBytes(bytes);

		String a = "", b = "";
		int lshift = 4, lshift_v = 0, r_v = 0;
		int max_shift = 4;

		for (int i = 0; i < bytes.length; ++i) {
			int v = bytes[i] & 0xff;

			a += padingLeft(Integer.toBinaryString(v), 8);

			r_v = (lshift_v << (8 - lshift)) | (v >>> lshift);
			lshift_v = v & ((1 << lshift) - 1);
			sb.append((char) r_v);

			b += padingLeft(Integer.toBinaryString(r_v), max_shift);
			if (lshift == max_shift) {
				sb.append((char) lshift_v);
				b += padingLeft(Integer.toBinaryString(lshift_v), max_shift);
				lshift = 4;
				lshift_v = 0;
			} else {
				++lshift;
			}
		}
		if (lshift > max_shift) {
			sb.append((char) lshift_v);
			b += padingLeft(Integer.toBinaryString(lshift_v), lshift - 1);
		}
		System.out.println(a);
		System.out.println(b);
		System.out.println(sb);

		/*
		ConsistentHashUtil hashUtil = new ConsistentHashUtil();
		int breakV = 15, end = 16, key_size = 102400;
		// 6结点
		for (int i = 0; i < breakV; ++i) {
			hashUtil.addNode(new Node("node:" + i, 20));
		}

		String keyPrefix = "aaaaaaaaaa";
		Map<String, String> result = new HashMap<>();

		{
			for (int i = 0; i < key_size; ++i) {
				String key = keyPrefix + ":" + i;

				Node nv = hashUtil.getNode(key);
				result.put(key, nv.id);
			}
		}

		// 补入2新结点
		for (int i = breakV; i < end; ++i) {
			hashUtil.addNode(new Node("node:" + i, 3));
		}

		{
			int c = 0;
			for (String key : result.keySet()) {
				String oldId = result.get(key);

				Node nv = hashUtil.getNode(key);
				if (!oldId.equals(nv.id)) {
					// System.out.println((String.format("(%s) 发生变化，命中结点%s-->%s.", key, oldId, nv.id)));
					++c;
				}
			}
			System.out.printf("失效比：%s%% \n", new BigDecimal(c).divide(new BigDecimal(key_size), 6, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).toString());
		}*/
	}
}
