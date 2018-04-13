package com.ezweb.engine.balance.rule;

import com.ezweb.engine.balance.Server;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <一句话说明功能>
 * <功能详细描述>
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/13
 */
public class RoundRobinRule<T extends Server> extends AbsLoadBalanceRule<T> {
	private AtomicInteger index = new AtomicInteger();

	public RoundRobinRule() {
	}

	@Override
	protected <K> T chooseSelect(List<T> list, K k) {
		int i = this.index.getAndIncrement();
		i = i % list.size();
		return list.get(i);
	}
}
