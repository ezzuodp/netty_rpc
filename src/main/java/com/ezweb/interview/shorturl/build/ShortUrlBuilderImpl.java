package com.ezweb.interview.shorturl.build;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class ShortUrlBuilderImpl implements ShortUrlBuilder {
	private AtomicLong id = new AtomicLong();

	/**
	 * 将标准化URL转换为shortUrl.
	 */
	@Override
	public Optional<String> buildShortCode(String normalUrl) {
		return Optional.of(String.valueOf(id.incrementAndGet()));
	}

}
