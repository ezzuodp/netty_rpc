package com.ezweb.engine.util;

import com.google.common.hash.Hashing;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

	public static void main(String[] args) {
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
		}
	}
}
