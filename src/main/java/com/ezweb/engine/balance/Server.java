package com.ezweb.engine.balance;

/**
 * 初始（weight, currentWeight = 0 )
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/13
 */
public abstract class Server {
	private volatile int weight = 0;
	private volatile int currentWeight = 0;

	public Server(int weight) {
		this.weight = weight;
		this.currentWeight = 0;
	}

	public int weight() {
		return this.weight;
	}

	public void currentWeight(int cw) {
		this.currentWeight = cw;
	}

	public int currentWeight() {
		return this.currentWeight;
	}
}
