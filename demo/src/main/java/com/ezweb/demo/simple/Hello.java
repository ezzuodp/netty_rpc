package com.ezweb.demo.simple;

import com.ezweb.engine.rpc.annotation.RpcAsync;

import java.util.List;

/**
 * @author : zuodp
 * @version : 1.10
 */
@RpcAsync
public interface Hello<T> {
	TimeResult say(List<T> name, long curTime);
}
