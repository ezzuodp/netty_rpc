package com.ezweb.engine.balance;

/**
 * 有权重的服务
 * init:=> effectiveWeight = weight ,  effectiveWeight = 0
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/13
 */
public interface WeightServer extends Server {
	int weight();

	void currentWeight(int cw);

	int currentWeight();

	int effectiveWeight();

	void effectiveWeight(int ew);
}
