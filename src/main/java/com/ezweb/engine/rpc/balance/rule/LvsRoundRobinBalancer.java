package com.ezweb.engine.rpc.balance.rule;

import com.ezweb.engine.rpc.balance.Server;

import java.util.List;

/**
 * 按照 lvs 加权轮叫调度算法 实现的
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/13
 */
public class LvsRoundRobinBalancer<T extends Server> extends AbsLoadBalancer<T> {
	private volatile int i = -1;
	private volatile int cw = 0;
	private int gcd = 0;
	private int maxw = 0;

	public LvsRoundRobinBalancer() {
	}

	@Override
	public synchronized void addServer(T server) {
		super.addServer(server);

		if (server.weight() > 0) {
			if (gcd == 0) {
				this.i = -1;
				this.gcd = server.weight();
				this.maxw = server.weight();
				this.cw = 0;
			} else {
				this.gcd = gcd(this.gcd, server.weight());
				if (this.maxw < server.weight()) {
					this.maxw = server.weight();
				}
			}
		}
	}

	@Override
	protected T chooseImpl(List<T> list) {

		/*
		假设有一组服务器S = {S0, S1, …, Sn-1}，W(Si)表示服务器Si的权值，一个
		指示变量i表示上一次选择的服务器，指示变量cw表示当前调度的权值，max(S)
		表示集合S中所有服务器的最大权值，gcd(S)表示集合S中所有服务器权值的最大
		公约数。变量i初始化为-1，cw初始化为零。

		while (true) {
		  i = (i + 1) mod n;
		  if (i == 0) {
		     cw = cw - gcd(S);
		     if (cw <= 0) {
		       cw = max(S);
		       if (cw == 0)
		         return NULL;
		     }
		  }
		  if (W(S(i)) >= cw)
		    return Si;
		}
		*/
		int n = list.size();
		synchronized (this) {
			// 暂时 sync 多线程同时并发 cw , i 并发冲突.
			do {
				this.i = (this.i + 1) % n;
				if (i == 0) {
					cw -= this.gcd;
					if (cw <= 0) {
						cw = this.maxw;
						if (cw == 0) {
							return null;
						}
					}
				}
				T tmp = list.get(i);
				if (tmp.weight() >= cw) {
					return tmp;
				}
			} while (true);
		}
	}

	private int gcd(int x, int y) {
		int t = 0;
		for (; ; ) {
			t = (x % y);
			if (t > 0) {
				x = y;
				y = t;
			} else {
				return y;
			}
		}
	}
}
