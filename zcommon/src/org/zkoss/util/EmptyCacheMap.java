/* EmptyCacheMap.java

	Purpose:

	Description:

	History:
		Thu Feb 18 10:26:36 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.util;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * An empty cache map.
 *
 * @author jameschu
 * @since 9.6.0
 */
public class EmptyCacheMap<K, V> extends AbstractMap<K, V> implements Cache<K, V> {
	private static final long serialVersionUID = 20210218102636L;

	@Override
	public V put(K key, V value) {
		return null;
	}

	@Override
	public int getLifetime() {
		return 0;
	}

	@Override
	public void setLifetime(int lifetime) {
		// do nothing
	}

	@Override
	public int getMaxSize() {
		return 0;
	}

	@Override
	public void setMaxSize(int maxsize) {
		// do nothing
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return Collections.emptySet();
	}
}