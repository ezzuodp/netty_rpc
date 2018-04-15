package com.ezweb.engine.balance.rule;

import com.ezweb.engine.balance.Server;

import java.util.List;

/**
 * 按照 nginx 加权轮叫调度算法(删除了effectiveWeight)
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/13
 */
public class NginxRoundRobinBalancer<T extends Server> extends AbsLoadBalancer<T> {

	@Override
	protected T chooseImpl(List<T> list) {
		int n = list.size();
		int total = 0;
		T best = null;

		synchronized (this) {
			// 多线程同时修改一个 server.currentWeight 并发冲突.
			for (int i = 0; i < n; ++i) {
				T w = list.get(i);
				if (w.weight() <= 0) continue;


				w.currentWeight(w.currentWeight() + w.weight());
				total += w.weight();

				if (best == null || w.currentWeight() > best.currentWeight()) {
					best = w;
				}
			}

			if (best != null) {
				best.currentWeight(best.currentWeight() - total);
			}
		}

		return best;
	}
}
