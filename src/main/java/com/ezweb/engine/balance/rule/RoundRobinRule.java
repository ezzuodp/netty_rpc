package com.ezweb.engine.balance.rule;

import com.ezweb.engine.balance.Server;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮叫调度
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/13
 */
public class RoundRobinRule<T extends Server> extends AbsLoadBalanceRule<T> {
	private AtomicInteger last = new AtomicInteger(0);

	public RoundRobinRule() {
	}

	@Override
	protected T chooseImpl(List<T> list) {
		int n = list.size();
		int j = this.last.getAndIncrement();
		T sel = null;
		do {
			j = j % n;
			sel = list.get(j);
			if (sel.weight() > 0) {
				return sel;
			}
			--n;
			++j;
		} while (n > 0);

		return sel;
	}
}
