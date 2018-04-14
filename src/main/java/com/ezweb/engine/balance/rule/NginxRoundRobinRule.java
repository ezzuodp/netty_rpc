package com.ezweb.engine.balance.rule;

import com.ezweb.engine.balance.Server;

import java.util.List;

/**
 * 按照 nginx 加权轮叫调度算法
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/13
 */
public class NginxRoundRobinRule<T extends Server> extends AbsLoadBalanceRule<T> {

	@Override
	protected T chooseImpl(List<T> list) {
		int n = list.size();
		int total = 0;
		T best = null;

		for (int i = 0; i < n; ++i) {
			T w = list.get(i);

			w.currentWeight(w.currentWeight() + w.effectiveWeight());
			total += w.effectiveWeight();

			// 由于有调用失败的情况,可以动态调低了有效权重
			if (w.effectiveWeight() < w.weight()) {
				// 负载时再动态把有效权重慢慢调起来
				w.incEffectiveWeight(1);
			}

			if (best == null || w.currentWeight() > best.currentWeight()) {
				best = w;
			}
		}

		if (best != null) {
			best.currentWeight(best.currentWeight() - total);
		}

		return best;
	}
}
