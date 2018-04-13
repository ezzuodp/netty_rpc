package com.ezweb.engine.balance.rule;

import com.ezweb.engine.balance.WeightServer;

import java.util.List;

/**
 * 按照 nginx 算法实现的权重软轮询算法
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/13
 */
public class WeightRoundRobinRule<T extends WeightServer> extends AbsLoadBalanceRule<T> {

	@Override
	protected <K> T chooseSelect(List<T> list, K k) {
		int total = 0;
		T best = null;

		for (int i = 0; i < list.size(); ++i) {
			T w = list.get(i);
			if (w == null) {
				continue;
			}

			w.currentWeight(w.currentWeight() + w.effectiveWeight());
			total += w.effectiveWeight();

			// 这句是 nginx 动态调低了有效权重后，再动态把有效权重调起来
			if (w.effectiveWeight() < w.weight()) {
				w.effectiveWeight(w.effectiveWeight() + 1);
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
