package com.ezweb.engine.balance.rule;

import com.ezweb.engine.balance.LoadBalanceRule;
import com.ezweb.engine.balance.Server;

import java.util.List;

/**
 * <一句话说明功能>
 * <功能详细描述>
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/13
 */
public abstract class AbsLoadBalanceRule<T extends Server> implements LoadBalanceRule<T> {
	@Override
	public T choose(List<T> list) {
		if (list.size() == 0) return null;
		if (list.size() == 1) {
			T sel = list.get(0);
			if (sel.weight() > 0) {
				return sel;
			}
			return null;
		}

		return chooseImpl(list);
	}

	protected abstract T chooseImpl(List<T> list);
}
