/* MultiCache.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 29 17:29:45     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
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
public class MultiCache<K, V> implements Cache<K, V>, java.io.Serializable, Cloneable {
	private final CacheMap<K, V>[] _caches;
	private int _maxsize, _lifetime;

	/** Constructs a multi cache with 17 inital caches.
	 */
	public MultiCache() {
		this(17);
	}
	/** Constucts a multi cache with the specified number of internal caches,
	 * the max size and the lifetime.
	 *
	 * @param nCache the postive number of the internal caches.
	 * The large the number the fast the performance.
	 * @param maxSize the maximal allowed size of each cache
	 */
	@SuppressWarnings("unchecked")
	public MultiCache(int nCache, int maxSize, int lifetime) {
		if (nCache <= 0)
			throw new IllegalArgumentException();
		_caches = (CacheMap<K, V>[])new CacheMap[nCache];
		_maxsize = maxSize;
		_lifetime = lifetime;
	}
	/** Constructs a multi cache with the specified number of internal caches.
	 *
	 * <p>The default lifetime is {@link #DEFAULT_LIFETIME}, and
	 * the default maximal allowed size of each cache is
	 * ({@link #DEFAULT_MAX_SIZE} / 10).
	 *
	 * @param nCache the postive number of the internal caches.
	 * The large the number the fast the performance.
	 */
	public MultiCache(int nCache) {
		this(nCache, DEFAULT_MAX_SIZE / 10, DEFAULT_LIFETIME);
	}

	//Cache//
	public boolean containsKey(Object key) {
		final CacheMap<K, V> cache = getCache(key);
		synchronized (cache) {
			return cache.containsKey(key);
		}
	}
	public V get(Object key) {
		final CacheMap<K, V> cache = getCache(key);
		synchronized (cache) {
			return cache.get(key);
		}
	}
	public V put(K key, V value) {
		final CacheMap<K, V> cache = getCache(key);
		synchronized (cache) {
			return cache.put(key, value);
		}
	}
	public V remove(Object key) {
		final CacheMap<K, V> cache = getCache(key);
		synchronized (cache) {
			return cache.remove(key);
		}
	}
	public void clear() {
		synchronized (this) {
			for (int j = 0; j < _caches.length; ++j)
				_caches[j] = null;
		}
	}

	private CacheMap<K, V> getCache(Object key) {
		int j = Objects.hashCode(key);
		j = (j >= 0 ? j: -j) % _caches.length;

		CacheMap<K, V> cache = _caches[j];
		if (cache == null)
			synchronized (this) {
				cache = _caches[j];
				if (cache == null) {
					cache = new CacheMap<K, V>(4);
					cache.setMaxSize(_maxsize);
					cache.setLifetime(_lifetime);
					_caches[j] = cache;
				}
			}
		return cache;
	}

	public int getLifetime() {
		return _lifetime;
	}
	public void setLifetime(int lifetime) {
		_lifetime = lifetime;

		for (int j = 0; j < _caches.length; ++j)
			if (_caches[j] != null)
				synchronized (_caches[j]) {
					_caches[j].setLifetime(lifetime);
				}
	}
	public int getMaxSize() {
		return _maxsize;
	}
	public void setMaxSize(int maxsize) {
		_maxsize = maxsize;

		for (int j = 0; j < _caches.length; ++j)
			if (_caches[j] != null)
				synchronized (_caches[j]) {
					_caches[j].setMaxSize(maxsize);
				}
	}

	//Cloneable//
	public Object clone() {
		MultiCache clone = new MultiCache(_caches.length, _maxsize, _lifetime);
		for (int j = 0; j < _caches.length; ++j)
			if (_caches[j] != null)
				synchronized (_caches[j]) {
					clone._caches[j] = (CacheMap)_caches[j].clone();
				}
		return clone;
	}
}
