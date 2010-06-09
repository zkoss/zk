/* ThreadLocalCache.java

	Purpose:
		
	Description:
		
	History:
		Thu Sep  6 16:33:52     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import java.util.Map;

/**
 * A cache that resides on the thread local memory.
 * The performance is excellent since no need to synchronize the access.
 * However, it takes more memory since each thread has its own map.
 * In addition, it might cause hot re-deployment failed to free old
 * classes if the map stores the references of the classes loaded by
 * the Web Application class loader.
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
	/** Constructs a thread-local cache with the default setting:
	 * max size=128 and lifetime=30minutes.
	 */
	public ThreadLocalCache() {
		this(DEFAULT_LIFETIME, 128);
	}

	//extra//
	/** Returns whether the cache for the current thread is empty.
	 */
	public boolean isEmpty() {
		return getCache().isEmpty();
	}
	/** Puts all object cached in the current thread to the specifed map.
	 */
	public void copyTo(Map map) {
		map.putAll(getCache());
	}

	//Cache//
	public boolean containsKey(Object key) {
		return getCache().containsKey(key);
	}
	public Object get(Object key) {
		return getCache().get(key);
	}
	public Object put(Object key, Object value) {
		return getCache().put(key, value);
	}
	public Object remove(Object key) {
		return getCache().remove(key);
	}
	public void clear() {
		getCache().clear();
	}

	private CacheMap getCache() {
		CacheMap cache = (CacheMap)_cache.get();
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
	/**
	 * Returns the maximal allowed size.
	 *
	 * <p>Defalut: 128 (it is smaller than most cache since this cache
	 * one per thread).
	 *
	 * An mapping won't be removed by GC unless the minimal lifetime
	 * or the maximal allowed size exceeds.
	 * @see #getLifetime
	 */
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
