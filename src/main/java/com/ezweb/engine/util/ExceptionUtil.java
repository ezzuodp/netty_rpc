/*
 *  Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ezweb.engine.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异常处理类
 *
 * @author dengpeng.zuo
 * @version 1.0
 */
public class ExceptionUtil {
	private final static Logger LOGGER = LoggerFactory.getLogger(ExceptionUtil.class);

	private static final StackTraceElement[] REMOTE_MOCK_STACK = new StackTraceElement[]{
			new StackTraceElement("remoteClass", "remoteMethod", "remoteFile", 1)
	};

	/**
	 * 覆盖给定exception的stack信息，server端产生业务异常时调用此类屏蔽掉server端的异常栈。
	 */
	private static void setDefaultStackTrace(Throwable e) {
		if (e != null) {
			try {
				e.setStackTrace(REMOTE_MOCK_STACK);
			} catch (Exception e1) {
				LOGGER.warn("replace remote exception stack fail!" + e1.getMessage());
			}
		}
	}

	public static void fillExceptionStackTrace(Throwable e, String className, String methodName) {
		// 优化堆栈信息，只保留
		if (e != null) {
			StackTraceElement[] xx = e.getStackTrace();
			int i = xx == null ? -1 : xx.length - 1;
			for (; i >= 0; --i) {
				if (xx[i].getClassName().equals(className) && xx[i].getMethodName().equals(methodName)) {
					break;
				}
			}
			if (i < 0) {
				setDefaultStackTrace(e);
			} else {
				StackTraceElement[] opt = new StackTraceElement[i + 1];
				System.arraycopy(xx, 0, opt, 0, i + 1);

				try {
					e.setStackTrace(opt);
				} catch (Exception e1) {
					LOGGER.warn("replace remote exception stack fail!" + e1.getMessage());
				}
			}
		}
	}
}
