/* FastReadCache.java

	History:
		Thu Jan 19 11:36:10 TST 2012, Created by tomyeh

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.util;

import java.util.Map;
import java.util.LinkedHashMap;
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
	private Map<K, V> _writeCache;
	private transient short _missCnt;
	/** whether _writeCache is different from _cache. */
	private boolean _moreInWriteCache;

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
		boolean found = _cache.containsKey(key);
		if (!found && _moreInWriteCache)
			synchronized (this) {
				if (_writeCache != null && (found = _writeCache.containsKey(key)))
					missed();
			}
		return found;
	}
	@Override
	public V get(Object key) {
		V val = _cache.get(key);
		if (val == null && _moreInWriteCache)
			synchronized (this) {
				if (_writeCache != null && (val = _writeCache.get(key)) != null)
					missed();
			}
		return val;
	}
	@Override
	public V put(K key, V value) {
		V result = value;
		synchronized (this) {
			if (!Objects.equals(value, _cache.get(key))) {
				result = syncToWriteCache().put(key, value);
				_moreInWriteCache = true;

				if (_cache.containsKey(key)) //ensure _writeCache >= _cache
					syncToReadCache();
			}
		}
		return result;
	}
	@Override
	public V remove(Object key) {
		V result = null;
		synchronized (this) {
			if (!_cache.containsKey(key) && !_moreInWriteCache)
				return null; //not found at all

			result = syncToWriteCache().remove(key);
			if (_cache.containsKey(key)) //ensure _writeCache >= _cache
				syncToReadCache();
		}
		return result;
	}
	@Override
	public void clear() {
		synchronized (this) {
			setReadAndClearWrite(new InnerCache(getMaxSize(), getLifetime()));
		}
	}

	//synchronized(this) before calling this
	private void missed() {
		//If missed too many times, we copy _writeCache back to _cache
		//
		//Note: we don't count it a miss if both _writeCache and _cache don't have
		//because it implies the same thread (i.e., only  a few thread,
		//so synchronized(this) overhead is small)
		if (++_missCnt == 100)
			syncToReadCache();
	}
	/** Synchronizes _writeCache to _cache.
	 ** <p>synchronized(this) before calling this
	 */
	private void syncToReadCache() {
		//don't check _moreInWriteCache here because it might be called
		//in remove() (when _writeCache is less but still need to sync)

		_missCnt = 0;
		_moreInWriteCache = false;

		final InnerCache cache = new InnerCache(getMaxSize(), getLifetime());
		cache.putAll(_writeCache);
		_cache = cache;
		//no need free to free _writeCache, since write faster (GC will clear it in expunge)
	}
	/** Synchronized from _cache to _writeCache.
	 ** <p>synchronized(this) before calling this
	 */
	private Map<K,V> syncToWriteCache() {
		if (_writeCache == null) {
			_writeCache = new LinkedHashMap<K, V>();
				//order is important because cache's expunge depends on it
			_writeCache.putAll(_cache);
		}
		return _writeCache;
	}
	//synchronized(this) before calling this
	private void setReadAndClearWrite(InnerCache cache) {
		_writeCache = null;
		_missCnt = 0;
		_moreInWriteCache = false;
		_cache = cache;
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
					final InnerCache cache = (InnerCache)this.clone();
					for (final K key: _removed)
						cache.remove(key);
					setReadAndClearWrite(cache);
				}
			}
			_removed = null;
			return result;
		}
	}
}
