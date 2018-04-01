package com.ezweb.interview.shorturl.build;

import java.util.Optional;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class ShortUrlBuilderImpl implements ShortUrlBuilder {
	/**
	 * 将标准化URL转换为shortUrl.
	 */
	@Override
	public Optional<String> buildShortCode(String normalUrl) {
		// <<normal_url, short_code>>
		return null;
	}

	/**
	 * 从持久化中加载已经生成的shortCode
	 */
	@Override
	public Optional<String> loadShortCode(String normalUrl) {
		return null;
	}

	/**
	 * 从持久化中根据shortCode加载标准URL.
	 */
	@Override
	public Optional<String> loadNormalUrl(String shortCode) {
		return null;
	}
}
