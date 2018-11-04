package com.ezweb.engine.rpc.balance.rule;

import com.ezweb.engine.rpc.balance.Server;
import com.google.common.collect.Maps;

import java.util.List;

/**
 * 按照 nginx 加权轮叫调度算法(删除了effectiveWeight)
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/13
 */
public class NginxRoundRobinBalancer<T extends Server> extends AbsLoadBalancer<T> {
	private java.util.concurrent.ConcurrentMap<Server, WeightValue> currentWeight = Maps.newConcurrentMap();

	@Override
	public void addServer(T server) {
		super.addServer(server);
		if (server.weight() > 0)
			this.currentWeight.put(server, new WeightValue(0));
	}

	@Override
	protected T chooseImpl(List<T> list) {
		int n = list.size();
		int total = 0;
		T best = null;
		WeightValue best_v = null;

		for (int i = 0; i < n; ++i) {
			T w = list.get(i);
			if (w.weight() <= 0) continue;

			WeightValue wv = currentWeight.get(w);
			wv.increment(w.weight());

			total += w.weight();

			if (best == null || wv.get() > best_v.get()) {
				best = w;
				best_v = wv;
			}
		}

		if (best != null) {
			best_v = currentWeight.get(best);
			best_v.decrement(total);
		}

		return best;
	}

	private static class WeightValue {
		int currentWeight;

		public WeightValue(int currentWeight) {
			this.currentWeight = currentWeight;
		}

		public void increment(int v) {
			this.currentWeight += v;
		}

		public void decrement(int v) {
			this.currentWeight -= v;
		}

		public int get() {
			return currentWeight;
		}
	}
}
