/* ThreadLocalCache.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep  6 16:33:52     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

/**
 * A cache that resides on the thread local memory.
 * The performance is excellent since no need to synchronize the access.
 * However, it takes more memory since each thread has its own map.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class ThreadLocalCache implements Cache {
	private final ThreadLocal _cache = new ThreadLocal();
	private int _maxsize, _lifetime;

	/** Constucts a thread-local cache with the specified max size
	 * and the lifetime.
	 */
	public ThreadLocalCache(int maxSize, int lifetime) {
		_maxsize = maxSize;
		_lifetime = lifetime;
	}
	/** Constructs a thread-local cache with the default setting.
	 */
	public ThreadLocalCache() {
		_lifetime = DEFAULT_LIFETIME;
		_maxsize = DEFAULT_MAXSIZE;
	}

	//Cache//
	public boolean containsKey(Object key) {
		return getCache(key).containsKey(key);
	}
	public Object get(Object key) {
		return getCache(key).get(key);
	}
	public Object put(Object key, Object value) {
		return getCache(key).put(key, value);
	}
	public Object remove(Object key) {
		return getCache(key).remove(key);
	}
	private Cache getCache(Object key) {
		Cache cache = (Cache)_cache.get();
		if (cache == null)
			_cache.set(cache = new CacheMap(_maxsize, _lifetime));
		return cache;
	}

	public int getLifetime() {
		return _lifetime;
	}
	public void setLifetime(int lifetime) {
		final Cache cache = (Cache)_cache.get();
		if (cache != null)
			cache.setLifetime(lifetime);
		_lifetime = lifetime;
	}
	public int getMaxSize() {
		return _maxsize;
	}
	public void setMaxSize(int maxsize) {
		final Cache cache = (Cache)_cache.get();
		if (cache != null)
			cache.setMaxSize(maxsize);
		_maxsize = maxsize;
	}
}
