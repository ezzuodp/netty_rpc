package com.ezweb.engine.balance.rule;

import com.ezweb.engine.balance.Server;

import java.util.List;

/**
 * 按照 lvs 加权轮叫调度算法
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/13
 */
public class LvsRoundRobinRule<T extends Server> extends AbsLoadBalanceRule<T> {
	private int i;
	private int gcd;
	private int maxW;

	@Override
	protected T chooseImpl(List<T> list) {
		initGcdMaxW(list);
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
		return null;
	}

	private void initGcdMaxW(List<T> list) {
		this.i = 0;
		this.maxW = 0;
		for (int i = 0; i < list.size(); ++i) {
			T iv = list.get(i);
			if (iv.weight() <= 0) continue;

			if (this.gcd == 0) {
				this.gcd = iv.weight();
			} else {
				this.gcd = gcd(gcd, iv.weight());
			}

			if (maxW < iv.weight())
				maxW = iv.weight();
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
