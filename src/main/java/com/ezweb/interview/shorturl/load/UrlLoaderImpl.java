package com.ezweb.interview.shorturl.load;

import com.ezweb.interview.RBTree;
import com.ezweb.interview.RBTreeItem;
import com.ezweb.interview.shorturl.url.NormalUrl;
import com.ezweb.interview.shorturl.url.ShortUrl;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class UrlLoaderImpl implements UrlLoader {

	private RBTree<String, DbShortUrlItem> shortDb = new RBTree<>();
	private RBTree<String, DbNormalUrlItem> normalDb = new RBTree<>();

	private LoadingCache<String/*normalUrl.Key*/, String/*shortCode*/> normalUrlCache = CacheBuilder.newBuilder()
			.expireAfterAccess(30, TimeUnit.MINUTES)
			.build(new CacheLoader<String, String>() {
				@Override
				public String load(String normalKey) throws Exception {
					Optional<DbNormalUrlItem> item = normalDb.find(normalKey);
					return item.get().shortCode;
				}
			});

	private LoadingCache<String/*shortCode*/, String/*normalUrl*/> shortUrlCache = CacheBuilder.newBuilder()
			.expireAfterAccess(30, TimeUnit.MINUTES)
			.build(new CacheLoader<String, String>() {
				@Override
				public String load(String shortCode) throws Exception {
					Optional<DbShortUrlItem> item = shortDb.find(shortCode);
					return item.get().normalUrl;
				}
			});

	/**
	 * 先从缓存，再从持久化中加载已经生成的shortCode
	 */
	@Override
	public Optional<String> loadShortCode(NormalUrl normalUrl) {
		return Optional.ofNullable(normalUrlCache.getIfPresent(normalUrl.key()));
	}

	/**
	 * 先从缓存，再从持久化中根据shortCode加载标准URL.
	 */
	@Override
	public Optional<String> loadNormalUrl(ShortUrl shortUrl) {
		return Optional.ofNullable(shortUrlCache.getIfPresent(shortUrl.getShortCode()));
	}

	/**
	 * 缓存对照关系.
	 */
	@Override
	public void saveUrlMapping(ShortUrl shortUrl, NormalUrl url) {
		shortDb.insert(new DbShortUrlItem(shortUrl.getShortCode(), url.getUrl()));
		normalDb.insert(new DbNormalUrlItem(url.key(), shortUrl.getShortCode()));
	}

	private static class DbShortUrlItem implements RBTreeItem<String> {
		private String shortCode;
		private String normalUrl;

		public DbShortUrlItem(String shortCode, String normalUrl) {
			this.shortCode = shortCode;
			this.normalUrl = normalUrl;
		}

		/**
		 * 对象KEY
		 */
		@Override
		public String key() {
			return shortCode;
		}

		/**
		 * 比较对象KEY
		 */
		@Override
		public int compareTo(String shortCode) {
			return this.shortCode.compareTo(shortCode);
		}
	}

	private static class DbNormalUrlItem implements RBTreeItem<String> {
		private String normalUrlKey;
		private String shortCode;

		public DbNormalUrlItem(String normalUrlKey, String shortCode) {
			this.normalUrlKey = normalUrlKey;
			this.shortCode = shortCode;
		}

		/**
		 * 对象KEY
		 */
		@Override
		public String key() {
			return normalUrlKey;
		}

		/**
		 * 比较对象KEY
		 */
		@Override
		public int compareTo(String normalUrlKey) {
			return this.normalUrlKey.compareTo(normalUrlKey);
		}
	}
}
