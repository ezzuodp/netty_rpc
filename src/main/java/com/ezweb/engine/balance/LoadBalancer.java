package com.ezweb.engine.balance;

import java.util.List;

/**
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/13
 */
public interface LoadBalancer<K, T extends Server> {
	T select(List<T> servers, K key);
}
