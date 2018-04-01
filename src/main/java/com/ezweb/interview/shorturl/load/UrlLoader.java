package com.ezweb.interview.shorturl.load;

import java.util.Optional;

/**
 * @author : zuodp
 * @version : 1.10
 */
public interface UrlLoader {
	/**
	 * 从持久化中加载已经生成的shortCode
	 */
	Optional<String> loadShortCode(String normalUrl);

	/**
	 * 从持久化中根据shortCode加载标准URL.
	 */
	Optional<String> loadNormalUrl(String shortCode);
}
