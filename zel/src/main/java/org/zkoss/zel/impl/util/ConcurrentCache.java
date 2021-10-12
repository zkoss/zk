/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zkoss.zel.impl.util;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class ConcurrentCache<K,V> {

    private final int size;

    private final Map<K,V> eden;

    private final Map<K,V> longterm;

    public ConcurrentCache(int size) {
        this.size = size;
        this.eden = new ConcurrentHashMap<K, V>(size);
        this.longterm = new WeakHashMap<K, V>(size);
    }

    public V get(K k) {
        V v = this.eden.get(k);
        if (v == null) {
            synchronized (longterm) {
                v = this.longterm.get(k);
            }
            if (v != null) {
                this.eden.put(k, v);
            }
        }
        return v;
    }

    public void put(K k, V v) {
        if (this.eden.size() >= size) {
            synchronized (longterm) {
                this.longterm.putAll(this.eden);
            }
            this.eden.clear();
        }
        this.eden.put(k, v);
    }

	// copied from ConcurrentMap
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		Objects.requireNonNull(mappingFunction);
		V oldValue, newValue;
		return ((oldValue = get(key)) == null && (newValue = mappingFunction.apply(key)) != null
			&& (oldValue = putIfAbsent(key, newValue)) == null) ? newValue : oldValue;
	}

	public V putIfAbsent(K key, V value) {
		if (this.eden.size() >= size) {
			synchronized (longterm) {
				this.longterm.putAll(this.eden);
			}
			this.eden.clear();
		}
		return this.eden.putIfAbsent(key, value);
	}
}
