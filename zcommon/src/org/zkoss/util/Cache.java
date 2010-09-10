/* Cache.java

	Purpose:
		
	Description:
		
	History:
		Thu Sep  6 15:35:06     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

/**
 * Represents a cache.
 * The interface is similar to java.util.Map but simpler to implement.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public interface Cache<K,V> {
	/** Returns whether the specified key is stored.
	 */
	public boolean containsKey(Object key);
	/** Returns the object of the specified key, or null if not found.
	 */
	public V get(Object key);
	/** Stores an object to the cache.
	 * @return the previous value of the same, or null if no such value
	 */
	public V put(K key, V value);
	/** Removes an object from the cache.
	 * @return the object if found.
	 */
	public V remove(Object key);
	/** Clears all objects being cached.
	 */
	public void clear();

	//Control//
	/** The default minimal lifetime, unit=milliseconds. It is 30 minutes. */
	public static final int DEFAULT_LIFETIME = 30 * 60 * 1000;
	/** The default maximal allowed size. It is 512. */
	public static final int DEFAULT_MAX_SIZE = 512;

	/**
	 * Returns the minimal lifetime, unit=milliseconds.
	 * An mapping won't be removed by GC unless the minimal lifetime
	 * or the maximal allowed size exceeds.
	 * @see #getMaxSize
	 */
	public int getLifetime();
	/**
	 * Sets the minimal lifetime. Default: {@link #DEFAULT_LIFETIME}.
	 *
	 * @param lifetime the lifetime, unit=milliseconds;
	 * if non-posive, they will be removed immediately.
	 * @see #getLifetime
	 */
	public void setLifetime(int lifetime);
	/**
	 * Returns the maximal allowed size. Defalut: {@link #DEFAULT_MAX_SIZE}.
	 * An mapping won't be removed by GC unless the minimal lifetime
	 * or the maximal allowed size exceeds.
	 * @see #getLifetime
	 */
	public int getMaxSize();
	/**
	 * Sets the maximal allowed size.
	 * @see #getMaxSize
	 */
	public void setMaxSize(int maxsize);
}
