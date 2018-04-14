package com.ezweb.engine.balance;

/**
 * 初始（effective_weight = weight, currentWeight = 0 )
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/13
 */
public class Server {
	private int weight = 0;
	private int effectiveWeight = 0;
	private int currentWeight = 0;

	public Server(int weight) {
		this.weight = weight;
		this.effectiveWeight = weight;
		this.currentWeight = 0;
	}

	/**
	 * 服务初始权重
	 */
	public int weight() {
		return this.weight;
	}

	/**
	 * 当前服务权重
	 */
	public int effectiveWeight() {
		return this.effectiveWeight;
	}

	public void incEffectiveWeight(int st) {
		this.effectiveWeight += st;
	}

	public void currentWeight(int cw) {
		this.currentWeight = cw;
	}

	public int currentWeight() {
		return this.currentWeight;
	}
}
