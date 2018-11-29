package com.ezweb.demo.simple;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 不要编辑，系统自动生成的 !
 * (2018-11-30 07:09:39.179)
 */
public interface HelloAsync<T> {
  CompletableFuture<TimeResult> say(List<T> name, long curTime);
}
