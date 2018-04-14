package com.ezweb.engine.balance;

import java.util.List;

/**
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/13
 */
public interface LoadBalanceRule<T extends Server> {
	T choose(List<T> list);
}
