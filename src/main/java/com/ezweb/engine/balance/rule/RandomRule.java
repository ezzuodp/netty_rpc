package com.ezweb.engine.balance.rule;

import com.ezweb.engine.balance.Server;

import java.util.List;
import java.util.Random;

/**
 * <一句话说明功能>
 * <功能详细描述>
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/13
 */
public class RandomRule<T extends Server> extends AbsLoadBalanceRule<T> {
	private java.util.Random random = null;

	public RandomRule() {
		this.random = new Random(System.currentTimeMillis());
	}

	@Override
	protected <K> T chooseSelect(List<T> list, K k) {
		int i = Math.abs(random.nextInt());
		i = i % list.size();
		return list.get(i);
	}
}
