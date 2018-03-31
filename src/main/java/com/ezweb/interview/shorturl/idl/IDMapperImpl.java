package com.ezweb.interview.shorturl.idl;

import com.ezweb.interview.RBTree;

import java.util.Optional;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class IDMapperImpl implements IDMapper {
	private RBTree<Long, Id2UrlItem> id2Url = new RBTree<>();

	@Override
	public void url2Id(long id, String url) {
		id2Url.insert(new Id2UrlItem(id, url));
	}

	@Override
	public String id2Url(long id) {
		Optional<Id2UrlItem> url = id2Url.find(id);
		if (url.isPresent()) {
			return url.get().getUrl();
		}
		return "";
	}

	private static class Id2UrlItem implements RBTree.RBTreeItem<Long> {
		private long shortId;
		private String url;

		public Id2UrlItem(long shortId, String url) {
			this.shortId = shortId;
			this.url = url;
		}

		public String getUrl() {
			return url;
		}

		@Override
		public Long key() {
			return Long.valueOf(this.shortId);
		}

		@Override
		public int compareTo(Long otherKey) {
			return Long.compare(this.key(), otherKey);
		}
	}
}
