package com.ezweb.interview.shorturl.build;

import com.ezweb.interview.shorturl.encode.H64;
import com.ezweb.interview.shorturl.url.NormalUrl;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class ShortUrlBuilderImpl implements ShortUrlBuilder {
	// TODO:实现一个高并发的号码生成器
	private AtomicLong id = new AtomicLong();

	/**
	 * 将标准化URL转换为shortUrl.
	 */
	@Override
	public Optional<String> buildShortCode(NormalUrl normalUrl) {
		return Optional.of(H64.long2Hex(id.incrementAndGet()));
	}
}
