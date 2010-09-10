/* CacheMap.java


	Purpose: 
	Description: 
	History:
	2001/11/23 15:26:21, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Map;
import java.util.AbstractSet;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;

import org.zkoss.lang.D;
import org.zkoss.lang.Objects;
import org.zkoss.util.logging.Log;

/**
 * The cache map. The key-to-value mappings hold in this map is
 * temporary. They are removed when GC demanding memory and a
 * criteria is met. The criteria is whether the mapping is old enough
 * (called lifetime), or the upper bound is hit (called max-size).
 *
 * <p>The criteria can be changed by overriding {@link #canExpunge}.
 * When to check the criteria can be changed by overriding
 * {@link #shallExpunge}.
 *
 * <p>If the criteria is totally independent of GC, you could override
 * {@link #shallExpunge} to always return true
 * (rather than when GC is activated).
 *
 * <p>It is different from WeakHashMap:
 *
 * <ul>
 *  <li>The mapping might be removed even if the key is hold somewhere
 *  (i.e., strong reachable).</li>
 *  <li>The mapping might not be removed when GC demanding memory
 *  if the criteria doesn't meet.</li>
 *  <li>It is not serializable.</li>
 * </ul>
 *
 * <p>Like other maps, it is not thread-safe. To get one, use
 * java.util.Collections.synchronizedMap.
 *
 * <p>Implementation Note: there is another version of CacheMap that
 * uses WeakReference for each value (refer to obsolete).
 * The drawback is that all mapping will be queued and need to be examined,
 * because GC tends to release all reference at once.
 *
 * <p>We don't use PhantomReference because it is still required to
 * re-create the reference after enqueued.
 *
 * @author tomyeh
 */
public class CacheMap<K,V> implements Map<K,V>, Cache<K,V>, java.io.Serializable, Cloneable {
    private static final long serialVersionUID = 20070907L;
	//private static final Log log = Log.lookup(CacheMap.class);

	/** The map to store the mappings. */
	private Map<K, Value<V>> _map; //it is OK to serialized
	/** The minimal lifetime. */
	private int _lifetime = DEFAULT_LIFETIME;
	/** The maximal allowed size. */
	private int _maxsize = DEFAULT_MAX_SIZE;
	/** The reference queue. */
	private transient ReferenceQueue<X> _que;
	/** The reference. */
	private transient WeakReference<X> _ref;
	/** A flag used for debug purpose. */
	private transient boolean _inExpunge;

	/** The class to be hold in the reference (to know GC is demanding). */
	private static class X {
	}
	/** The class to hold key/value. */
	protected static final class Value<V> implements java.io.Serializable, Cloneable {
		private V value;
		private long access; //when the mapping is accessed

		/** Creates an instance to store in the map. */
		private Value(V value) {
			this.value = value;
			updateAccessTime();
		}

		private final void updateAccessTime() {
			this.access = System.currentTimeMillis();
		}

		//-- utilities--//
		/** Returns the value. */
		public final V getValue() {
			return this.value;
		}
		/** Returns the last access time. */
		public final long getAccessTime() {
			return this.access;
		}

		//-- cloneable --//
		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				throw new InternalError();
			}
		}
		//-- Object --//
		public final String toString() {
			return "(" + this.value + '@' + this.access + ')';
		}
	}

	//-- deriving to override --//
	/**
	 * Called when a pair of key and value having been expunged.
	 * This method is called after it is removed, so you could
	 * add it back.
	 *
	 * <p>Default: does nothing
	 */
	protected void onExpunge(Value v) {
	}

	/** Returns by {@link #canExpunge} to denote it shall not be expunged. */
	protected static final int EXPUNGE_NO = 0x0;
	/** Returns by {@link #canExpunge} to denote it shall be expunged. */
	protected static final int EXPUNGE_YES = 0x1; //must not zero
	/** Returns by {@link #canExpunge} to denote the searching of the
	 * next mapping shall continue.
	 */
	protected static final int EXPUNGE_CONTINUE = 0x0;
	/** Returns by {@link #canExpunge} to denote the searching of the
	 * next mapping shall stop.
	 */
	protected static final int EXPUNGE_STOP = 0x2; //must not zero

	/** Returns whether it is time to expunge.
	 * Once shallExpunge returns true, values are examined one-by-one thru
	 * {@link #canExpunge}, and expunged if EXPUNGE_YES.
	 *
	 * <p>This implementation returns true only if GC was activated.
	 * You might override it to return true, such that expunge is enforced
	 * no matter GC was activated.
	 *
	 * @see #canExpunge
	 */
	protected boolean shallExpunge() {
		return _que == null || _que.poll() != null;
	}
	/**
	 * Tests whether certain value is OK to expunge.
	 *
	 * <p>Note: values are tested thru {@link #canExpunge} only if
	 * {@link #shallExpunge} returns true.
	 *
	 * <p>Deriving classes might override this method to return different
	 * value for different criteria.
	 *
	 * <p>The return value coulde be a combination of EXPUNGE_xxx.
	 * One of EXPUNGE_YES and EXPUNGE_NO is returned to denote
	 * whether to expunge the mapping. One of EXPUNGE_CONTINUE and
	 * EXPUNGE_STOP is returned to denote whether to continue the
	 * searching of the next mapping for expunging.
	 *
	 * <p>Normally, you return either (EXPUNGE_YES|EXPUNGE_CONTINUE)
	 * or (EXPUNG_NO|EXPUNGE_STOP).
	 * Notice that the mapping is queried in the last-access order.
	 * Thus, you rarely needs to return (EXPUNGE_NO|EXPUNGE_CONTINUE)
	 * unless the appropriate one might be out of this order.
	 *
	 * <p>This implementation compares the access time and size.
	 * It returns (EXPUNGE_YES|EXPUNGE_CONTINUE) if OK, and
	 * (EXPUNGE_NO|EXPUNGE_STOP) if not.
	 *
	 * @return a combination of EXPUNGE_xxx
	 * @see #shallExpunge
	 */
	protected int canExpunge(Value v) {
		return _map.size() > getMaxSize()
			|| (System.currentTimeMillis() - v.access) > getLifetime() ?
			(EXPUNGE_YES|EXPUNGE_CONTINUE): (EXPUNGE_NO|EXPUNGE_STOP);
	}
	/** Expunges if {@link #shallExpunge} is true. */
	private void tryExpunge() {
		if (shallExpunge()) {
			if (_inExpunge)
				throw new IllegalStateException("expung in expung?");
			try {
				expunge();
			} finally {
				newRef();
			}
		}
	}
	/** Enforces to expunge items that exceeds the maximal allowed number
	 * or lifetime.
	 * <p>By default, this method is called only GC takes places.
	 * @return number of items left ({@link #size}) after expunged
	 * @since 3.6.1
	 */
	public int expunge() {
		if (_inExpunge || _map.isEmpty()) return _map.size(); //nothing to do

		_inExpunge = true;
		try {
			//dennis, bug 1815633, remove some control code here 
			for (final Iterator<Value<V>> it = _map.values().iterator();it.hasNext();) {
				final Value<V> v = it.next();
				final int result = canExpunge(v);
				if ((result & EXPUNGE_YES) != 0) {
					//if (D.ON && log.debugable())
					//	log.debug("expunge: value="+v.value+" size="+_map.size()+"("+getMaxSize()+") time="+v.access+"("+getLifetime()+")");

					it.remove();
					onExpunge(v);
				}

				if ((result & EXPUNGE_STOP) != 0)
					break; //stop
			}
			return _map.size();
		} finally {
			_inExpunge = false;
		}
	}
	/** Re-create the reference so we can detect if GC was activated.
	 */
	private void newRef() {
		if (_que != null)
			_ref = new WeakReference<X>(new X(), _que);
	}

	//-- constructors --//
	/** Constructs a cache map with the specified max size and lifetime.
	 * @since 3.0.0
	 */
	public CacheMap(int maxSize, int lifetime) {
		this();
		setMaxSize(maxSize);
		setLifetime(lifetime);
	}
	/** Constructs a cachemap by using LinkedHashMap internally.
	 */
	public CacheMap() {
		_map = new LinkedHashMap<K, Value<V>>(16, 0.75f, true);
		init();
	}
	/** Constructs a cachemap by using LinkedHashMap internally.
	 */
	public CacheMap(int cap) {
		_map = new LinkedHashMap<K, Value<V>>(cap, 0.75f, true);
		init();
	}
	/** Constructs a cachemap by using LinkedHashMap internally.
	 */
	public CacheMap(int cap, float load) {
		_map = new LinkedHashMap<K, Value<V>>(cap, load, true);
		init();
	}
	/** Initialization for contructor and de-serialized. */
	private void init() {
		_que = new ReferenceQueue<X>();
		newRef();
	}

	//-- extra api --//
	/**
	 * Gets the minimal lifetime, unit=milliseconds.
	 * An mapping won't be removed by GC unless the minimal lifetime
	 * or the maximal allowed size exceeds.
	 * @see #getMaxSize
	 */
	public int getLifetime() {
		return _lifetime;
	}
	/**
	 * Sets the minimal lifetime. Default: {@link #DEFAULT_LIFETIME}.
	 *
	 * @param lifetime the lifetime, unit=milliseconds;
	 * if non-posive, they will be removed immediately.
	 * @see #getLifetime
	 */
	public void setLifetime(int lifetime) {
		_lifetime = lifetime;
	}
	/**
	 * Gets the maximal allowed size. Defalut: {@link #DEFAULT_MAX_SIZE}.
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
	 */
	public void setMaxSize(int maxsize) {
		_maxsize = maxsize;
	}

	//-- Map --//
	public boolean isEmpty() {
		tryExpunge();
		return _map.isEmpty();
	}
	/** Returns whether it is empty without trying to expunge first.
	 * @since 3.0.1
	 */
	public boolean isEmptyWithoutExpunge() {
		return _map.isEmpty();
	}
	public int size() {
		tryExpunge();
		return _map.size();
	}
	/** Returns the size without trying to expunge first.
	 * @since 3.0.1
	 */
	public int sizeWithoutExpunge() {
		return _map.size();
	}
	public void clear() {
		_map.clear();
	}

	public V remove(Object key) {
		tryExpunge();
		final Value<V> v = _map.remove(key);
		return v != null ? v.value: null;
	}
	public V get(Object key) {
		tryExpunge();
		return getWithoutExpunge(key);
	}
	/** Returns the value without trying to expunge first.
	 * It is useful if you want to preserve all entries.
	 */
	public V getWithoutExpunge(Object key) {
		final Value<V> v = _map.get(key); //re-order
		if (v != null) {
			v.updateAccessTime();
			//assertion(key);
			return v.value;
		}
		return null;
	}
	public boolean containsKey(Object key) {
		tryExpunge();
		return _map.containsKey(key);
	}
	public boolean containsValue(Object value) {
		tryExpunge();
		for (Value<V> v: _map.values()) {
			if (Objects.equals(value, v.value))
				return true;
		}
		return false;
	}
	public V put(K key, V value) {
		tryExpunge();
		final Value<V> v = _map.put(key, new Value<V>(value));
		return v != null ? v.value: null;
	}
	public void putAll(java.util.Map<? extends K,? extends V> map) {
		for (Map.Entry<? extends K, ? extends V> me: map.entrySet())
			put(me.getKey(), me.getValue());
	}

	/** It wraps what is stored in _map, such that the caller
	 * won't know the value is wrapped with Value.
	 */
	private static class Entry<K,V> implements Map.Entry<K, V> {
		final Map.Entry<K,Value<V>> _me;

		@SuppressWarnings("unchecked")
		private Entry(Map.Entry me) {
			_me = me;
		}
		public K getKey() {
			return _me.getKey();
		}
		public V getValue() {
			return _me.getValue().value;
		}
		public V setValue(V o) {
			//we don't re-order it to avoid comodification error
			final Value<V> v = _me.getValue();
			final V old = v.value;
			v.value = o;
			return old;
		}

		//-- Object --//
		public int hashCode() {
			return _me.hashCode();
		}
		public boolean equals(Object o) {
			return (o instanceof Entry) && _me.equals(((Entry)o)._me);
		}
	}
	/** Abstract iterator. */
	private static class KeyIter implements Iterator {
		private Iterator _it;
		private KeyIter(Iterator it) {
			_it = it;
		}
		public boolean hasNext() {
			return _it.hasNext();
		}
		public void remove() {
			_it.remove(); //remove from map
		}
		public Object next() {
			return _it.next();
		}
	}
	/** Entry iterator. Don't call expunge to avoid co-modified exception. */
	private static class EntryIter extends KeyIter {
		private EntryIter(Iterator it) {
			super(it);
		}
		public Object next() {
			return new Entry((Map.Entry)super.next());
		}
	}
	/** The entry set. */
	private class EntrySet extends AbstractSet {
		private EntrySet() {
		}
		//-- Set --//
		public Iterator iterator() {
			tryExpunge();
			return new EntryIter(_map.entrySet().iterator());
		}
		public boolean contains(Object o) {
			return (o instanceof Map.Entry)
				&& CacheMap.this.containsKey(((Map.Entry)o).getKey());
		}
		public boolean remove(Object o) {
			return (o instanceof Map.Entry)
				&& CacheMap.this.remove(((Map.Entry)o).getKey()) != null;
		}
		public int size() {
			return CacheMap.this.size();
		}
		public void clear() {
			CacheMap.this.clear();
		}
	}
	@SuppressWarnings("unchecked")
	public Set<Map.Entry<K,V>> entrySet() {
		tryExpunge();
		return new EntrySet();
	}

	/** The entry set. */
	private class KeySet extends AbstractSet {
		private KeySet() {
		}
		//-- Set --//
		public Iterator iterator() {
			tryExpunge();
			return new KeyIter(_map.keySet().iterator());
		}
		public boolean contains(Object o) {
			return CacheMap.this.containsKey(o);
		}
		public boolean remove(Object o) {
			return CacheMap.this.remove(o) != null;
		}
		public int size() {
			return CacheMap.this.size();
		}
		public void clear() {
			CacheMap.this.clear();
		}
	}
	@SuppressWarnings("unchecked")
	public Set<K> keySet() {
		tryExpunge();
		return new KeySet();
	}

	/** Value iterator. Don't call expunge to avoid co-modified exception. */
	private static class ValueIter extends KeyIter {
		private ValueIter(Iterator it) {
			super(it);
		}
		public Object next() {
			return ((Value)super.next()).value;
		}
	}
	/** The value collection. */
	private class Values extends AbstractCollection {
		public Iterator iterator() {
			return new ValueIter(_map.values().iterator());
		}
		public int size() {
			return CacheMap.this.size();
		}
		public boolean contains(Object o) {
			return CacheMap.this.containsValue(o);
		}
		public void clear() {
			CacheMap.this.clear();
		}
	}
	@SuppressWarnings("unchecked")
	public Collection<V> values() {
		tryExpunge();
		return new Values();
	}

	//-- Object --//
	public int hashCode() {
		tryExpunge();
		return _map.hashCode();
	}
	public boolean equals(Object o) {
		tryExpunge();
		return o == this
			|| ((o instanceof CacheMap) && _map.equals(((CacheMap)o)._map))
			|| ((o instanceof Map) && _map.equals((Map)o));
	}
	public String toString() {
		tryExpunge();

		final StringBuffer sb = new StringBuffer(128).append('{');
		if (!_map.isEmpty()) {
			for (final Iterator it = _map.entrySet().iterator();;) {
				final Map.Entry me = (Map.Entry)it.next();
				sb.append(me.getKey()).append('=')
					.append(Objects.toString(((Value)me.getValue()).value));
				if (it.hasNext()) sb.append(", ");
				else break; //done
			}
		}
		return sb.append('}').toString();
	}

	//-- Debug --//
	/** To make sure that it is in the acess order. */
	/*private final void assertion(Object key) {
		long last = Long.MIN_VALUE;
		int j = 0;
		for (final Iterator it = _map.values().iterator(); it.hasNext(); ++j) {
			final Value v = (Value)it.next();
			assert v.access >= last: "Order is wrong: j="+j+" key="+key+" acs="+v.access+" map="+_map;
			last = v.access;
		}
	}*/

	//Cloneable//
	@SuppressWarnings("unchecked")
	public Object clone() {
		final CacheMap<K,V> clone;
		try {
			clone = (CacheMap<K,V>)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}

		clone._map = new LinkedHashMap<K, Value<V>>(clone._map);
		for (Map.Entry<K, Value<V>> me: clone._map.entrySet()) {
			me.setValue((Value<V>)me.getValue().clone());
		}

		clone.init();
		return clone;
	}

	//Serializable//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		init();
	}
}
