package com.ezweb.engine.rpc.balance.rule;

import com.ezweb.engine.rpc.balance.Server;

import java.util.List;
import java.util.Random;

/**
 * 随机调度
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/13
 */
public class RandomBalancer<T extends Server> extends AbsLoadBalancer<T> {
	private java.util.Random random = null;

	public RandomBalancer() {
		this.random = new Random(System.currentTimeMillis());
	}

	@Override
	protected T chooseImpl(List<T> list) {
		int n = list.size();
		int randomWeight = Math.abs(random.nextInt());
		for (int i = 0; i < n; ++i) {
			T iv = list.get(i);
			if (iv.weight() > 0) {
				randomWeight -= iv.weight();
				if (randomWeight <= 0) {
					return iv;
				}
			}
		}
		return null;
	}
}
