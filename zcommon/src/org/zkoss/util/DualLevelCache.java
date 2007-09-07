/* DualLevelCache.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Sep  7 14:48:27     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A cache that uses an {@link ThreadLocalCache} instance as front end,
 * and a {@link CacheMap} instance as the backend.
 *
 * <p>Like {@link ThreadLocalCache}, it doesn't require any synchronization.
 * Unlike {@link ThreadLocalCache}, when the cache is cleared ({@link #clear}),
 * all cached object are merged to the backend.
 *
 * <p>In other words, if the user calls {@link #clear} at the right moment,
 * the memory use will be minimized (comparing to {@link ThreadLocalCache}),
 * while it maintains a similar performance (taking two hash accesses).
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class DualLevelCache extends ThreadLocalCache {
	/** Represents an object is removed from the front cache. */
	private static final Object REMOVED = new Object();

	private CacheMap _shared;

	/** Constucts a thread-local cache with the specified max size
	 * and the lifetime.
	 */
	public DualLevelCache(int maxsize, int lifetime) {
		super(toFrontSize(maxsize), lifetime);
		_shared = new CacheMap(maxsize, lifetime);
	}
	private static int toFrontSize(int maxsize) {
		maxsize >>= 3;
		return maxsize < 4 && maxsize >= 0 ? 4: maxsize;
	}
	/** Constructs a thread-local cache with the default setting.
	 */
	public DualLevelCache() {
		this(DEFAULT_MAX_SIZE, DEFAULT_LIFETIME);
	}

	//Cache//
	public boolean containsKey(Object key) {
		final Object o = super.get(key); //front
		if (o == REMOVED) return false;
		if (o != null) return true;
		return _shared.containsKey(key) || super.containsKey(key);
	}
	public Object get(Object key) {
		final Object o = super.get(key);
		if (o != null || super.containsKey(key))
			return o != REMOVED ? o: null;
		return _shared.get(key);
	}
	public Object put(Object key, Object value) {
		final Object o = super.put(key, value);
		if (o != null || super.containsKey(key))
			return o != REMOVED ? o: null;
		return _shared.get(key);
	}
	public Object remove(Object key) {
		final Object o = super.put(key, REMOVED);
		if (o != null || super.containsKey(key))
			return o != REMOVED ? o: null;
		return _shared.get(key);
	}
	public void clear() {
		final Map map = new HashMap();
		super.copyTo(map);
		super.clear();

		CacheMap clone = null;
		for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			final Object key = me.getKey();
			final Object value = me.getValue();

			if (clone == null) {
				if (value == REMOVED) {
					if (!_shared.containsKey(key)) continue;
				} else {
					if (_shared.get(key) == value
					&& (value != null || _shared.containsKey(key)))
						continue;
				}
				clone =  new CacheMap(_shared.getMaxSize(), _shared.getLifetime());
				clone.putAll(_shared);
			}

			if (value == REMOVED) clone.remove(key);
			else clone.put(key, value);
		}

		if (clone != null)
			_shared = clone;
	}
	public boolean isEmpty() {
	//Not very accurate since the front one might have nothing but REMOVED
		return super.isEmpty() && _shared.isEmpty();
	}

	public int getLifetime() {
		return _shared.getLifetime();
	}
	public void setLifetime(int lifetime) {
		synchronized (this) {
			_shared.setLifetime(lifetime);
		}
		super.setLifetime(lifetime);
	}
	public int getMaxSize() {
		return _shared.getMaxSize();
	}
	public void setMaxSize(int maxsize) {
		synchronized (this) {
			_shared.setMaxSize(maxsize);
		}
		super.setMaxSize(toFrontSize(maxsize));
	}
}
