package com.ezweb.engine.rpc.balance;

/**
 * 初始（weight, currentWeight = 0 )
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/13
 */
public abstract class Server {
	private volatile int weight = 0;

	public Server(int weight) {
		this.weight = weight;
	}

	public int weight() {
		return this.weight;
	}
}
