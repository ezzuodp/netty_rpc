package com.ezweb.engine.balance.rule;

import com.ezweb.engine.balance.LoadBalanceRule;
import com.ezweb.engine.balance.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/13
 */
public abstract class AbsLoadBalancer<T extends Server> implements LoadBalanceRule<T> {
	private volatile List<T> serverList = null;

	public AbsLoadBalancer() {
		this.serverList = new ArrayList<>(16);
	}

	public void addServer(T server) {
		if (server == null) return;

		List<T> newList = new ArrayList<>(this.getServerList());
		newList.add(server);

		this.serverList = newList;
	}

	@Override
	public T choose() {
		List<T> serverList = getServerList();
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

	protected List<T> getServerList() {
		return this.serverList;
	}

	protected abstract T chooseImpl(List<T> list);
}
