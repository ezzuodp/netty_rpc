package com.ezweb.engine.rpc.simple;

/**
 * @author : zuodp
 * @version : 1.10
 */
public abstract class PrefixUtils {

	public static final String DEFAULT = "/NoPrefix";

	public static String buildServiceUrl(Class<?> classType) {
		return DEFAULT + "/" + classType;
	}

	public static String buildServiceUrl(String prefix, Class<?> classType) {
		return prefix + "/" + classType.getName();
	}

	public static String buildRefUrl(String className) {
		return DEFAULT + "/" + className;
	}

	public static String buildRefUrl(String prefix, String className) {
		return prefix + "/" + className;
	}
}
