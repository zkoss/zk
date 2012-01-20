/* FastReadCache.java

	History:
		Thu Jan 19 11:36:10 TST 2012, Created by tomyeh

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.util;

import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import org.zkoss.lang.Objects;

/**
 * A {@link CacheMap} that the possiblity to have cache hit is much more than
 * not. It maintains a reaonly cache (so no need to synchronize), and then
 * clone and replace it if there is a miss.
 * Thus, as time goes, most access can go directly to the readonly cache
 * without any synchronization or cloning.
 * <p>Thread safe.
 * @author tomyeh
 * @since 6.0.0
 */
public class FastReadCache<K, V> implements Cache<K, V>, java.io.Serializable, Cloneable {
	private InnerCache _cache;

	/** Constructor.
	 */
	public FastReadCache() {
		this(DEFAULT_MAX_SIZE, DEFAULT_LIFETIME);
	}
	/** Constructor.
	 */
	public FastReadCache(int maxSize, int lifetime) {
		_cache = new InnerCache(maxSize, lifetime);
	}

	@Override
	public boolean containsKey(Object key) {
		return _cache.containsKey(key);
	}
	@Override
	public V get(Object key) {
		return _cache.get(key);
	}
	@Override
	public V put(K key, V value) {
		V result = value;
		synchronized (this) {
			if (!Objects.equals(value, _cache.get(key))) {
				InnerCache cache = (InnerCache)_cache.clone();
				result = cache.put(key, value);
				_cache = cache;
			}
		}
		return result;
	}
	@Override
	public V remove(Object key) {
		V result = null;
		synchronized (this) {
			if (_cache.containsKey(key)) {
				InnerCache cache = (InnerCache)_cache.clone();
				result = cache.remove(key);
				_cache = cache;
			}
		}
		return result;
	}
	@Override
	public void clear() {
		synchronized (this) {
			if (!_cache.isEmpty())
				_cache = new InnerCache(getMaxSize(), getLifetime());
		}
	}

	@Override
	public int getLifetime() {
		return _cache.getLifetime();
	}
	@Override
	public void setLifetime(int lifetime) {
		_cache.setLifetime(lifetime);
	}
	@Override
	public int getMaxSize() {
		return _cache.getMaxSize();
	}
	@Override
	public void setMaxSize(int maxsize) {
		_cache.setMaxSize(maxsize);
	}

	private class InnerCache extends CacheMap<K, V> {
		private List<K> _removed;

		private InnerCache() {
		}
		private InnerCache(int maxSize, int lifetime) {
			super(maxSize, lifetime);
		}
		@Override /*package*/
		void removeInExpunge(Iterator<Map.Entry<K, Value<V>>> it, K key) {
			_removed.add(key);
				//don't remove it here since _cache is readonly
		}
		@Override
		public int expunge() {
			_removed = new ArrayList<K>();
			final int result = super.expunge();

			if (!_removed.isEmpty()) {
				synchronized (FastReadCache.this) {
					InnerCache cache = (InnerCache)_cache.clone();
					for (final K key: _removed)
						cache.remove(key);
					_cache = cache;
				}
			}
			_removed = null;
			return result;
		}
	}
}
