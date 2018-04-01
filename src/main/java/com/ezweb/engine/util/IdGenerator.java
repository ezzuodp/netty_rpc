/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.ezweb.engine.util;

import com.google.common.base.Preconditions;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

/**
 * Default distributed primary key generator.
 * <p>
 * <p>
 * Use snowflake algorithm. Length is 64 bit.
 * </p>
 * <p>
 * <pre>
 * 1bit   sign bit.
 * 41bits timestamp offset from 2016.11.01(Sharding-JDBC distributed primary key published data) to now.
 * 10bits worker process id.
 * 12bits auto increment offset in one mills
 * </pre>
 * <p>
 * <p>
 * Call @{@code DefaultKeyGenerator.setWorkerId} to set.
 * </p>
 *
 * @author gaohongtao
 */
public final class IdGenerator {

	public static final long EPOCH;

	private static final long SEQUENCE_BITS = 12L;

	public static final long WORKER_ID_BITS = 10L;

	private static final long SEQUENCE_MASK = (1 << SEQUENCE_BITS) - 1;

	private static final long WORKER_ID_LEFT_SHIFT_BITS = SEQUENCE_BITS;

	private static final long TIMESTAMP_LEFT_SHIFT_BITS = WORKER_ID_LEFT_SHIFT_BITS + WORKER_ID_BITS;

	public static final int WORKER_ID_MAX_VALUE = 1 << WORKER_ID_BITS;
	public static final int WORKER_ID_MAX_MARK = WORKER_ID_MAX_VALUE - 1;

	static {
		EPOCH = LocalDateTime.of(2018, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC).toEpochMilli();
	}

	private long workerId;

	private long sequence;

	private long lastTime;

	public IdGenerator(long workerId) {
		this.workerId = (workerId & WORKER_ID_MAX_MARK);
	}

	/**
	 * Generate key.
	 *
	 * @return key type is @{@link Long}.
	 */
	public synchronized long generateKey() {
		long currentMillis = System.currentTimeMillis();
		Preconditions.checkState(lastTime <= currentMillis, "Clock is moving backwards, last time is %d milliseconds, current time is %d milliseconds", lastTime, currentMillis);
		if (lastTime == currentMillis) {
			if (0L == (sequence = ++sequence & SEQUENCE_MASK)) {
				currentMillis = waitUntilNextTime(currentMillis);
			}
		} else {
			sequence = 0;
		}
		lastTime = currentMillis;
		return ((currentMillis - EPOCH) << TIMESTAMP_LEFT_SHIFT_BITS) | (workerId << WORKER_ID_LEFT_SHIFT_BITS) | sequence;
	}

	private long waitUntilNextTime(final long lastTime) {
		long time = System.currentTimeMillis();
		while (time <= lastTime) {
			time = System.currentTimeMillis();
			try {
				TimeUnit.NANOSECONDS.sleep(lastTime - time);
			} catch (InterruptedException e) {
				// ignore e;
			}
		}
		return time;
	}
}
