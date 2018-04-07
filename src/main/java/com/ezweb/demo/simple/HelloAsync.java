package com.ezweb.demo.simple;

import java.util.concurrent.CompletableFuture;

/**
 * @author : zuodp
 * @version : 1.10
 */
public interface HelloAsync {
	CompletableFuture<TimeResult> say(String name, long curTime);
}
