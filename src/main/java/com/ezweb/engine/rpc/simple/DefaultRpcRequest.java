package com.ezweb.engine.rpc.simple;

import com.ezweb.engine.rpc.RpcRequest;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class DefaultRpcRequest implements RpcRequest {
	private String interfaceName;
	private String methodName;
	private String methodDesc;
	private Object[] arguments;

	@Override
	public String getInterfaceName() {
		return interfaceName;
	}

	@Override
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	@Override
	public String getMethodName() {
		return methodName;
	}

	@Override
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	@Override
	public String getMethodDesc() {
		return methodDesc;
	}

	@Override
	public void setMethodDesc(String methodDesc) {
		this.methodDesc = methodDesc;
	}

	@Override
	public Object[] getArguments() {
		return arguments;
	}

	@Override
	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}
}
