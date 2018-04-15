package com.ezweb.engine.balance.rule;

import com.ezweb.engine.balance.LoadBalanceRule;
import com.ezweb.engine.balance.Server;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/13
 */
public abstract class AbsLoadBalancer<T extends Server> implements LoadBalanceRule<T> {
	private List<T> serverList = null;

	public AbsLoadBalancer() {
		this.serverList = new ArrayList<>();
	}

	public void addServer(T server) {
		this.beforeAddServer(serverList, server);
		this.serverList.add(server);
		this.afterAddServer(serverList, server);
	}

	protected void beforeAddServer(List<T> serverList, T server) {

	}

	protected void afterAddServer(List<T> serverList, T server) {

	}

	@Override
	public T choose() {
		if (serverList.size() == 0) return null;
		if (serverList.size() == 1) {
			T sel = serverList.get(0);
			if (sel.weight() > 0) {
				return sel;
			}
			return null;
		}

		return chooseImpl(serverList);
	}

	protected abstract T chooseImpl(List<T> list);
}
