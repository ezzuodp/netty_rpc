package com.ezweb.engine.balance;

import java.util.List;

/**
 * <一句话说明功能>
 * <功能详细描述>
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/13
 */
public interface LoadBalanceRule<T extends Server> {
	<K> T choose(List<T> list, K k);
}
