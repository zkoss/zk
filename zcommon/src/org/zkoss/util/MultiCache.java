/* MultiCache.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Aug 29 17:29:45     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import org.zkoss.lang.Objects;

/**
 * A {@link CacheMap}-based cache.
 * It creates multiple instances of {@link CacheMap}, called
 * the internal caches, and then distributes the access across them.
 * Thus, the performance is porportional to the number of internal caches.
 *
 * <p>Thread safe.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class MultiCache implements java.io.Serializable, Cloneable {
	private final CacheMap[] _caches;
	private int _maxsize;

	/** Constructs a multi cache with the specified number of internal caches.
	 *
	 * @param nCache the postive number of the internal caches.
	 * The large the number the fast the performance.
	 */
	public MultiCache(int nCache) {
		if (nCache <= 0)
			throw new IllegalArgumentException("Positive only");

		_caches = new CacheMap[nCache];
		for (int j = 0; j < nCache; ++j) {
			_caches[j] = new CacheMap(8);
			_maxsize += _caches[j].getMaxSize();
		}
	}
	/* Used by {@link #clone} only. */
	private MultiCache(CacheMap[] clone, int maxsize) {
		_maxsize = maxsize;
		_caches = new CacheMap[clone.length];
		for (int j = 0; j < clone.length; ++j)
			_caches[j] = (CacheMap)clone[j].clone();
	}
	/** Constructs a multi cache with the specified number of internal caches
	 * and the initialize size.
	 *
	 * @param nCache the postive number of the internal caches.
	 * The large the number the fast the performance.
	 * @param initSize the initialize size
	 */
	public MultiCache(int nCache, int initSize) {
		if (nCache <= 0 || initSize <= 0)
			throw new IllegalArgumentException("Positive only");

		initSize = (initSize - 1) / nCache + 1;
		_caches = new CacheMap[nCache];
		for (int j = 0; j < nCache; ++j) {
			_caches[j] = new CacheMap(initSize);
			_maxsize += _caches[j].getMaxSize();
		}
	}
	
	/** Returns whether the specified key is stored.
	 */
	public boolean containsKey(Object key) {
		final CacheMap map = getCache(key);
		synchronized (map) {
			return map.containsKey(key);
		}
	}
	/** Returns the object.
	 */
	public Object get(Object key) {
		final CacheMap map = getCache(key);
		synchronized (map) {
			return map.get(key);
		}
	}
	/** Stores an object to the cache
	 */
	public Object put(Object key, Object value) {
		final CacheMap map = getCache(key);
		synchronized (map) {
			return map.put(key, value);
		}
	}
	private CacheMap getCache(Object key) {
		int hc = Objects.hashCode(key);
        hc = (hc >>> 13) ^ hc;
		if (hc < 0) hc = -hc;
		return _caches[hc % _caches.length];
	}

	//Control//
	/**
	 * Gets the minimal lifetime, unit=milliseconds.
	 * An mapping won't be removed by GC unless the minimal lifetime
	 * or the maximal allowed size exceeds.
	 * @see #getMaxSize
	 */
	public int getLifetime() {
		return _caches[0].getLifetime();
	}
	/**
	 * Sets the minimal lifetime. Default: {@link CacheMap#DEFAULT_LIFETIME}.
	 *
	 * @param lifetime the lifetime, unit=milliseconds;
	 * if non-posive, they will be removed immediately.
	 * @see #getLifetime
	 * @return this object
	 */
	public MultiCache setLifetime(int lifetime) {
		synchronized (this) {
			for (int j = 0; j < _caches.length; ++j)
				_caches[j].setLifetime(lifetime);
		}
		return this;
	}
	/**
	 * Gets the maximal allowed size. Defalut: {@link CacheMap#DEFAULT_MAXSIZE}.
	 * An mapping won't be removed by GC unless the minimal lifetime
	 * or the maximal allowed size exceeds.
	 * @see #getLifetime
	 */
	public int getMaxSize() {
		return _maxsize;
	}
	/**
	 * Sets the maximal allowed size.
	 * @see #getMaxSize
	 * @return this object
	 */
	public MultiCache setMaxSize(int maxsize) {
		_maxsize = maxsize;

		int v = maxsize / _caches.length;
		if (v == 0)
			v = maxsize > 0 ? 1: maxsize < 0 ? -1: 0;

		synchronized (this) {
			for (int j = 0; j < _caches.length; ++j)
				_caches[j].setMaxSize(v);
		}
		return this;
	}

	//Cloneable//
	public Object clone() {
		return new MultiCache(_caches, _maxsize);
	}
}
