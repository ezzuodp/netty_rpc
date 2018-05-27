package com.ezweb.demo.simple;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author : zuodp
 * @version : 1.10
 */
public interface HelloAsync {
	CompletableFuture<TimeResult> say(List<String> name, long curTime);
}
