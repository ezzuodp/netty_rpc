package com.ezweb.engine.rpc.client;

import java.lang.reflect.Method;

/**
 * <一句话说明功能>
 * <功能详细描述>
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/8
 */
public abstract class HandlerUtils {
	static <T> boolean isLocalMethod(Class<T> clz, Method method) {
		if (method.getDeclaringClass().equals(Object.class)) {
			try {
				clz.getDeclaredMethod(method.getName(), method.getParameterTypes());
				return false;
			} catch (Exception e) {
				return true;
			}
		}
		return false;
	}
}
