package com.ezweb.interview.shorturl.build;

import java.util.Optional;

/**
 * @author : zuodp
 * @version : 1.10
 */
public interface ShortUrlBuilder {
	Optional<String> buildShortCode(String normalUrl);

	Optional<String> loadNormalUrl(String shortCode);

	Optional<String> loadShortCode(String normalUrl);
}
