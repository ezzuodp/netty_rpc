package com.ezweb.engine.rpc;

/**
 * @author zuodengpeng
 * @date 2017-08-07
 * @since 1.0.0
 */
public class RpcRequest {
	private String interfaceName;
	private String methodName;
	private String methodDesc;
	private Object[] arguments;

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getMethodDesc() {
		return methodDesc;
	}

	public void setMethodDesc(String methodDesc) {
		this.methodDesc = methodDesc;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}
}
