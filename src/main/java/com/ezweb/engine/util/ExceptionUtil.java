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
	public static void setMockStackTrace(Throwable e) {
		if (e != null) {
			try {
				e.setStackTrace(REMOTE_MOCK_STACK);
			} catch (Exception e1) {
				LOGGER.warn("replace remote exception stack fail!" + e1.getMessage());
			}
		}
	}

	public static void fillExceptionStackTrace(Throwable e, String className, String method) {
		// e.getStackTrace();

		// TODO:优化堆栈信息
		if (e != null) {
			try {
				e.setStackTrace(new StackTraceElement[]{
						new StackTraceElement(className, method, "remoteFile", 0)
				});
			} catch (Exception e1) {
				LOGGER.warn("replace remote exception stack fail!" + e1.getMessage());
			}
		}
	}
}
