package com.ezweb.engine.balance.impl;

import com.ezweb.engine.balance.LoadBalanceRule;
import com.ezweb.engine.balance.LoadBalancer;
import com.ezweb.engine.balance.Server;

import java.util.List;

/**
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/13
 */
public class LoadBalancerImpl<K, T extends Server> implements LoadBalancer<K, T> {
	private LoadBalanceRule<T> rule;

	public LoadBalancerImpl(LoadBalanceRule<T> rule) {
		this.rule = rule;
	}

	@Override
	public T select(List<T> servers, K key) {
		return this.rule.choose(servers, key);
	}
}
