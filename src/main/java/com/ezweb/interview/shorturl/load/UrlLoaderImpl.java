package com.ezweb.interview.shorturl.load;

import java.util.Optional;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class UrlLoaderImpl implements UrlLoader {

	/**
	 * 从持久化中加载已经生成的shortCode
	 */
	@Override
	public Optional<String> loadShortCode(String normalUrl) {
		return Optional.empty();
	}

	/**
	 * 从持久化中根据shortCode加载标准URL.
	 */
	@Override
	public Optional<String> loadNormalUrl(String shortCode) {
		return Optional.empty();
	}
}
