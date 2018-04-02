package com.ezweb.interview.shorturl.build;

import java.util.Optional;

/**
 * @author : zuodp
 * @version : 1.10
 */
public interface ShortUrlBuilder {
	Optional<String> buildShortCode(NormalUrl normalUrl);
}
