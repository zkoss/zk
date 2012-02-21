/* WeakIdentityMap.java

	Purpose:
		
	Description:
		
	History:
		Sep 2, 2011 11:04:57 AM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

/*
 *  Copyright 2004 Brian S O'Neill
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

//revised from cojen
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * WeakIdentityMap is like WeakHashMap, except it uses a key's identity
 * hashcode and equals methods. WeakIdentityMap is not thread-safe and must be
 * wrapped with Collections.synchronizedMap to be made thread-safe.
 * <p>
 * The documentation for WeakHashMap states that it is intended primarily for
 * use with key objects whose equals methods test for object identity using the
 * == operator. Because WeakIdentityMap strictly follows this behavior, it is
 * better suited for this purpose.
 * <p>
 * Note: Weakly referenced entries may be automatically removed during
 * either accessor or mutator operations, possibly causing a concurrent
 * modification to be detected. Therefore, even if multiple threads are only
 * accessing this map, be sure to synchronize this map first. Also, do not
 * rely on the value returned by size() when using an iterator from this map.
 * The iterators may return less entries than the amount reported by size().
 *
 * @author Brian S O'Neill
 * 
 * @since 6.0.0
 */
@SuppressWarnings("unchecked")
public class WeakIdentityMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {
    // Types of Iterators
    static final int KEYS = 0;
    static final int VALUES = 1;
    static final int ENTRIES = 2;

    /**
     * Converts a collection to string, supporting collections that contain
     * self references
     */
    static String toString(Collection c) {
        if (c.size() == 0) {
            return "[]";
        }
        StringBuffer buf = new StringBuffer(32 * c.size());
        buf.append('[');

        Iterator it = c.iterator();
        boolean hasNext = it.hasNext();
        while (hasNext) {
            Object obj = it.next();
            buf.append(obj == c ? "(this Collection)" : obj);
            hasNext = it.hasNext();
            if (hasNext) {
                buf.append(", ");
            }
        }
        buf.append("]");
        return buf.toString();
    }

    /**
     * Converts a map to string, supporting maps that contain self references
     */
    static String toString(Map m) {
        if (m.size() == 0) {
            return "{}";
        }
        StringBuffer buf = new StringBuffer(32 * m.size());
        buf.append('{');

        Iterator it = m.entrySet().iterator();
        boolean hasNext = it.hasNext();
        while (hasNext) {
            Map.Entry entry = (Map.Entry)it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            buf.append(key == m ? "(this Map)" : key)
               .append('=')
               .append(value == m ? "(this Map)" : value);

            hasNext = it.hasNext();
            if (hasNext) {
                buf.append(',').append(' ');
            }
        }

        buf.append('}');
        return buf.toString();
    }

    private transient Entry<K, V>[] table;
    private transient int count;
    private int threshold;
    private final float loadFactor;
    private final ReferenceQueue<K> queue;
    
    //this field volatile to detect concurrent modification, 
    //check line if (WeakIdentityMap.this.modCount != this.expectedModCount) {
    private transient volatile int modCount; 

    // Views

    private transient Set<K> keySet;
    private transient Set<Map.Entry<K, V>> entrySet;
    private transient Collection<V> values;

    public WeakIdentityMap(int initialCapacity, float loadFactor) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be greater than 0");
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Load factor must be greater than 0");
        }
        this.loadFactor = loadFactor;
        this.table = new Entry[initialCapacity];
        this.threshold = (int)(initialCapacity * loadFactor);
        this.queue = new ReferenceQueue();
    }

    public WeakIdentityMap(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public WeakIdentityMap() {
        this(11, 0.75f);
    }

    public WeakIdentityMap(Map<? extends K, ? extends V> t) {
        this(Math.max(2 * t.size(), 11), 0.75f);
        putAll(t);
    }

    public int size() {
        // Cleanup right before, to report a more accurate size.
        cleanup();
        return this.count;
    }

    public boolean isEmpty() {
        return this.count == 0;
    }

    public boolean containsValue(Object value) {
        Entry[] tab = this.table;

        if (value == null) {
            for (int i = tab.length ; i-- > 0 ;) {
                for (Entry e = tab[i], prev = null; e != null; e = e.next) {
                    if (e.get() == null) {
                        // Clean up after a cleared Reference.
                        this.modCount++;
                        if (prev != null) {
                            prev.next = e.next;
                        } else {
                            tab[i] = e.next;
                        }
                        this.count--;
                    } else if (e.value == null) {
                        return true;
                    } else {
                        prev = e;
                    }
                }
            }
        } else {
            for (int i = tab.length ; i-- > 0 ;) {
                for (Entry e = tab[i], prev = null; e != null; e = e.next) {
                    if (e.get() == null) {
                        // Clean up after a cleared Reference.
                        this.modCount++;
                        if (prev != null) {
                            prev.next = e.next;
                        } else {
                            tab[i] = e.next;
                        }
                        this.count--;
                    } else if (value.equals(e.value)) {
                        return true;
                    } else {
                        prev = e;
                    }
                }
            }
        }

        return false;
    }

    public boolean containsKey(Object key) {
        if (key == null) {
            key = KeyFactory.NULL;
        }

        Entry[] tab = this.table;
        int hash = System.identityHashCode(key);
        int index = (hash & 0x7fffffff) % tab.length;

        for (Entry e = tab[index], prev = null; e != null; e = e.next) {
            Object entryKey = e.get();

            if (entryKey == null) {
                // Clean up after a cleared Reference.
                this.modCount++;
                if (prev != null) {
                    prev.next = e.next;
                } else {
                    tab[index] = e.next;
                }
                this.count--;
            } else if (e.hash == hash && key == entryKey) {
                return true;
            } else {
                prev = e;
            }
        }

        return false;
    }

    public V get(Object key) {
        if (key == null) {
            key = KeyFactory.NULL;
        }

        Entry<K, V>[] tab = this.table;
        int hash = System.identityHashCode(key);
        int index = (hash & 0x7fffffff) % tab.length;

        for (Entry<K, V> e = tab[index], prev = null; e != null; e = e.next) {
            Object entryKey = e.get();

            if (entryKey == null) {
                // Clean up after a cleared Reference.
                this.modCount++;
                if (prev != null) {
                    prev.next = e.next;
                } else {
                    tab[index] = e.next;
                }
                this.count--;
            } else if (e.hash == hash && key == entryKey) {
                return e.value;
            } else {
                prev = e;
            }
        }

        return null;
    }

    private void cleanup() {
        // Cleanup after cleared References.
        Entry[] tab = this.table;
        ReferenceQueue queue = this.queue;
        Reference ref;
        while ((ref = queue.poll()) != null) {
            // Since buckets are single-linked, traverse entire list and
            // cleanup all cleared references in it.
            int index = (((Entry) ref).hash & 0x7fffffff) % tab.length;
            for (Entry e = tab[index], prev = null; e != null; e = e.next) {
                if (e.get() == null) {
                    this.modCount++;
                    if (prev != null) {
                        prev.next = e.next;
                    } else {
                        tab[index] = e.next;
                    }
                    this.count--;
                } else {
                    prev = e;
                }
            }
        }
    }

    private void rehash() {
        int oldCapacity = this.table.length;
        Entry[] oldMap = this.table;

        int newCapacity = oldCapacity * 2 + 1;
        if (newCapacity <= 0) {
            // Overflow.
            if ((newCapacity = Integer.MAX_VALUE) == oldCapacity) {
                return;
            }
        }
        Entry[] newMap = new Entry[newCapacity];

        this.modCount++;
        this.threshold = (int)(newCapacity * this.loadFactor);
        this.table = newMap;

        for (int i = oldCapacity ; i-- > 0 ;) {
            for (Entry old = oldMap[i] ; old != null ; ) {
                Entry e = old;
                old = old.next;

                // Only copy entry if its key hasn't been cleared.
                if (e.get() == null) {
                    this.count--;
                } else {
                    int index = (e.hash & 0x7fffffff) % newCapacity;
                    e.next = newMap[index];
                    newMap[index] = e;
                }
            }
        }
    }

    public V put(K key, V value) {
        if (key == null) {
            key = (K) KeyFactory.NULL;
        }

        cleanup();

        // Make sure the key is not already in the WeakIdentityMap.
        Entry[] tab = this.table;
        int hash = System.identityHashCode(key);
        int index = (hash & 0x7fffffff) % tab.length;

        for (Entry e = tab[index], prev = null; e != null; e = e.next) {
            Object entryKey = e.get();

            if (entryKey == null) {
                // Clean up after a cleared Reference.
                this.modCount++;
                if (prev != null) {
                    prev.next = e.next;
                } else {
                    tab[index] = e.next;
                }
                this.count--;
            } else if (e.hash == hash && key == entryKey) {
                Object old = e.value;
                e.value = value;
                return (V) old;
            } else {
                prev = e;
            }
        }

        this.modCount++;

        if (this.count >= this.threshold) {
            // Rehash the table if the threshold is still exceeded.
            rehash();
            tab = this.table;
            index = (hash & 0x7fffffff) % tab.length;
        }

        // Creates the new entry.
        Entry e = new Entry(hash, key, this.queue, value, tab[index]);
        tab[index] = e;
        this.count++;
        return null;
    }

    public V remove(Object key) {
        if (key == null) {
            key = KeyFactory.NULL;
        }

        Entry<K, V>[] tab = this.table;
        int hash = System.identityHashCode(key);
        int index = (hash & 0x7fffffff) % tab.length;

        for (Entry<K, V> e = tab[index], prev = null; e != null; e = e.next) {
            Object entryKey = e.get();

            if (entryKey == null) {
                // Clean up after a cleared Reference.
                this.modCount++;
                if (prev != null) {
                    prev.next = e.next;
                } else {
                    tab[index] = e.next;
                }
                this.count--;
            } else if (e.hash == hash && key == entryKey) {
                this.modCount++;
                if (prev != null) {
                    prev.next = e.next;
                } else {
                    tab[index] = e.next;
                }
                this.count--;

                V oldValue = e.value;
                e.value = null;
                return oldValue;
            } else {
                prev = e;
            }
        }

        return null;
    }

    public void putAll(Map<? extends K, ? extends V> t) {
        Iterator i = t.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry e = (Map.Entry) i.next();
            put((K) e.getKey(), (V) e.getValue());
        }
    }

    public void clear() {
        Entry[] tab = this.table;
        this.modCount++;
        for (int index = tab.length; --index >= 0; ) {
            tab[index] = null;
        }
        this.count = 0;
    }

    public Object clone() {
        try { 
            WeakIdentityMap t = (WeakIdentityMap)super.clone();
            t.table = new Entry[this.table.length];
            for (int i = this.table.length ; i-- > 0 ; ) {
                t.table[i] = (this.table[i] != null) 
                    ? (Entry)this.table[i].copy(this.queue) : null;
            }
            t.keySet = null;
            t.entrySet = null;
            t.values = null;
            t.modCount = 0;
            return t;
        }
        catch (CloneNotSupportedException e) { 
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    public Set<K> keySet() {
        if (this.keySet == null) {
            this.keySet = new AbstractSet<K>() {
                public Iterator iterator() {
                    return createHashIterator(KEYS);
                }
                public int size() {
                    return WeakIdentityMap.this.count;
                }
                public boolean contains(Object o) {
                    return containsKey(o);
                }
                public boolean remove(Object o) {
                    return o == null ? false : WeakIdentityMap.this.remove(o) == o;
                }
                public void clear() {
                    WeakIdentityMap.this.clear();
                }
                public String toString() {
                    return WeakIdentityMap.toString(this);
                }
            };
        }
        return this.keySet;
    }

    public Collection<V> values() {
        if (this.values==null) {
            this.values = new AbstractCollection<V>() {
                public Iterator<V> iterator() {
                    return createHashIterator(VALUES);
                }
                public int size() {
                    return WeakIdentityMap.this.count;
                }
                public boolean contains(Object o) {
                    return containsValue(o);
                }
                public void clear() {
                    WeakIdentityMap.this.clear();
                }
                public String toString() {
                    return WeakIdentityMap.toString(this);
                }
            };
        }
        return this.values;
    }

    public Set<Map.Entry<K, V>> entrySet() {
        if (this.entrySet==null) {
            this.entrySet = new AbstractSet<Map.Entry<K, V>>() {
                public Iterator<Map.Entry<K, V>> iterator() {
                    return createHashIterator(ENTRIES);
                }

                public boolean contains(Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry entry = (Map.Entry)o;
                    Object key = entry.getKey();

                    Entry[] tab = WeakIdentityMap.this.table;
                    int hash = System.identityHashCode(key);
                    int index = (hash & 0x7fffffff) % tab.length;

                    for (Entry e = tab[index], prev = null; e != null; e = e.next) {
                        Object entryKey = e.get();
                        
                        if (entryKey == null) {
                            // Clean up after a cleared Reference.
                            WeakIdentityMap.this.modCount++;
                            if (prev != null) {
                                prev.next = e.next;
                            } else {
                                tab[index] = e.next;
                            }
                            WeakIdentityMap.this.count--;
                        } else if (e.hash == hash && e.equals(entry)) {
                            return true;
                        } else {
                            prev = e;
                        }
                    }

                    return false;
                }

                public boolean remove(Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry entry = (Map.Entry)o;
                    Object key = entry.getKey();
                    Entry[] tab = WeakIdentityMap.this.table;
                    int hash = System.identityHashCode(key);
                    int index = (hash & 0x7fffffff) % tab.length;

                    for (Entry e = tab[index], prev = null; e != null; e = e.next) {
                        if (e.get() == null) {
                            // Clean up after a cleared Reference.
                            WeakIdentityMap.this.modCount++;
                            if (prev != null) {
                                prev.next = e.next;
                            } else {
                                tab[index] = e.next;
                            }
                            WeakIdentityMap.this.count--;
                        } else if (e.hash == hash && e.equals(entry)) {
                            WeakIdentityMap.this.modCount++;
                            if (prev != null) {
                                prev.next = e.next;
                            } else {
                                tab[index] = e.next;
                            }
                            WeakIdentityMap.this.count--;

                            e.value = null;
                            return true;
                        } else {
                            prev = e;
                        }
                    }
                    return false;
                }

                public int size() {
                    return WeakIdentityMap.this.count;
                }

                public void clear() {
                    WeakIdentityMap.this.clear();
                }

                public String toString() {
                    return WeakIdentityMap.toString(this);
                }
            };
        }

        return this.entrySet;
    }

    /**
     * Gets the map as a String.
     * 
     * @return a string version of the map
     */
    public String toString() {
        return toString(this);
    }

    private Iterator createHashIterator(int type) {
        if (this.count == 0) {
            return Collections.EMPTY_SET.iterator();
        } else {
            return new HashIterator(type);
        }
    }

    /**
     * WeakIdentityMap collision list entry.
     */
    private static class Entry<K, V> extends WeakReference<K> implements Map.Entry<K, V> {
        int hash;
        V value;
        Entry<K, V> next;

        Entry(int hash, K key, ReferenceQueue<K> queue, V value, Entry<K, V> next) {
            super(key, queue);
            this.hash = hash;
            this.value = value;
            this.next = next;
        }

        public void clear() {
            // Do nothing if reference is explicity cleared. This prevents
            // backdoor modification of map entries.
        }

        public K getKey() {
            K key = Entry.this.get();
            return key == KeyFactory.NULL ? null : key;
        }

        public V getValue() {
            return this.value;
        }

        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            return equals((Map.Entry)obj);
        }

        boolean equals(Map.Entry<K, V> e) {
            Object thisKey = get();
            if (thisKey == null) {
                return false;
            } else if (thisKey == KeyFactory.NULL) {
                thisKey = null;
            }
            return (thisKey == e.getKey()) &&
                (this.value == null ? e.getValue() == null : this.value.equals(e.getValue()));
        }

        public int hashCode() {
            return this.hash ^ (this.value == null ? 0 : this.value.hashCode());
        }

        public String toString() {
            return getKey() + "=" + this.value;
        }

        protected Object copy(ReferenceQueue queue) {
            return new Entry(this.hash, get(), queue, this.value,
                             (this.next == null ? null : (Entry)this.next.copy(queue)));
        }
    }

    private class HashIterator implements Iterator {
        private final int type;
        private final Entry[] table;

        private int index;

        // To ensure that the iterator doesn't return cleared entries, keep a
        // hard reference to the key. Its existence will prevent the weak
        // key from being cleared.
        Object entryKey;
        Entry entry;

        Entry last;

        /**
         * The modCount value that the iterator believes that the backing
         * List should have. If this expectation is violated, the iterator
         * has detected concurrent modification.
         */
        private int expectedModCount = WeakIdentityMap.this.modCount;

        HashIterator(int type) {
            this.table = WeakIdentityMap.this.table;
            this.type = type;
            this.index = table.length;
        }

        public boolean hasNext() {
            while (this.entry == null || (this.entryKey = this.entry.get()) == null) {
                if (this.entry != null) {
                    // Clean up after a cleared Reference.
                    remove(this.entry);
                    this.entry = this.entry.next;
                }
                else {
                    if (this.index <= 0) {
                        return false;
                    }
                    else {
                        this.entry = this.table[--this.index];
                    }
                }
            }

            return true;
        }

        public Object next() {
            if (WeakIdentityMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }

            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            this.last = this.entry;
            this.entry = this.entry.next;

            return this.type == KEYS ? this.last.getKey() :
                (this.type == VALUES ? this.last.getValue() : this.last);
        }

        public void remove() {
            if (this.last == null) {
                throw new IllegalStateException();
            }
            if (WeakIdentityMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            remove(this.last);
            this.last = null;
        }

        private void remove(Entry toRemove) {
            Entry[] tab = this.table;
            int index = (toRemove.hash & 0x7fffffff) % tab.length;

            for (Entry e = tab[index], prev = null; e != null; e = e.next) {
                if (e == toRemove) {
                    WeakIdentityMap.this.modCount++;
                    expectedModCount++;
                    if (prev == null) {
                        tab[index] = e.next;
                    } else {
                        prev.next = e.next;
                    }
                    WeakIdentityMap.this.count--;
                    return;
                } else {
                    prev = e;
                }
            }

            throw new ConcurrentModificationException();
        }

        public String toString() {
            if (this.last != null) {
                return "Iterator[" + this.last + ']';
            } else {
                return "Iterator[]";
            }
        }
    }
}
/*
 *  Copyright 2004 Brian S O'Neill
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


/**
 * KeyFactory generates keys which can be hashed or compared for any kind of
 * object including arrays, arrays of arrays, and null. All hashcode
 * computations, equality tests, and ordering comparsisons fully recurse into
 * arrays.
 *
 * @author Brian S O'Neill
 */
@SuppressWarnings("unchecked")
class KeyFactory {
	static final Object NULL = new Comparable() {
        public int compareTo(Object obj) {
            return obj == this || obj == null ? 0 : 1;
        }
    };

    public static Object createKey(boolean[] obj) {
        return obj == null ? NULL : new BooleanArrayKey(obj);
    }

    public static Object createKey(byte[] obj) {
        return obj == null ? NULL : new ByteArrayKey(obj);
    }

    public static Object createKey(char[] obj) {
        return obj == null ? NULL : new CharArrayKey(obj);
    }

    public static Object createKey(double[] obj) {
        return obj == null ? NULL : new DoubleArrayKey(obj);
    }

    public static Object createKey(float[] obj) {
        return obj == null ? NULL : new FloatArrayKey(obj);
    }

    public static Object createKey(int[] obj) {
        return obj == null ? NULL : new IntArrayKey(obj);
    }

    public static Object createKey(long[] obj) {
        return obj == null ? NULL : new LongArrayKey(obj);
    }

    public static Object createKey(short[] obj) {
        return obj == null ? NULL : new ShortArrayKey(obj);
    }

    public static Object createKey(Object[] obj) {
        return obj == null ? NULL : new ObjectArrayKey(obj);
    }

    public static Object createKey(Object obj) {
        if (obj == null) {
            return NULL;
        }
        if (!obj.getClass().isArray()) {
            return obj;
        }
        if (obj instanceof Object[]) {
            return createKey((Object[])obj);
        } else if (obj instanceof int[]) {
            return createKey((int[])obj);
        } else if (obj instanceof float[]) {
            return createKey((float[])obj);
        } else if (obj instanceof long[]) {
            return createKey((long[])obj);
        } else if (obj instanceof double[]) {
            return createKey((double[])obj);
        } else if (obj instanceof byte[]) {
            return createKey((byte[])obj);
        } else if (obj instanceof char[]) {
            return createKey((char[])obj);
        } else if (obj instanceof boolean[]) {
            return createKey((boolean[])obj);
        } else if (obj instanceof short[]) {
            return createKey((short[])obj);
        } else {
            return obj;
        }
    }

    static int hashCode(boolean[] a) {
        int hash = 0;
        for (int i = a.length; --i >= 0; ) {
            hash = (hash << 1) + (a[i] ? 0 : 1);
        }
        return hash == 0 ? -1 : hash;
    }

    static int hashCode(byte[] a) {
        int hash = 0;
        for (int i = a.length; --i >= 0; ) {
            hash = (hash << 1) + a[i];
        }
        return hash == 0 ? -1 : hash;
    }

    static int hashCode(char[] a) {
        int hash = 0;
        for (int i = a.length; --i >= 0; ) {
            hash = (hash << 1) + a[i];
        }
        return hash == 0 ? -1 : hash;
    }

    static int hashCode(double[] a) {
        int hash = 0;
        for (int i = a.length; --i >= 0; ) {
            long v = Double.doubleToLongBits(a[i]);
            hash = hash * 31 + (int)(v ^ v >>> 32);
        }
        return hash == 0 ? -1 : hash;
    }

    static int hashCode(float[] a) {
        int hash = 0;
        for (int i = a.length; --i >= 0; ) {
            hash = hash * 31 + Float.floatToIntBits(a[i]);
        }
        return hash == 0 ? -1 : hash;
    }

    static int hashCode(int[] a) {
        int hash = 0;
        for (int i = a.length; --i >= 0; ) {
            hash = (hash << 1) + a[i];
        }
        return hash == 0 ? -1 : hash;
    }

    static int hashCode(long[] a) {
        int hash = 0;
        for (int i = a.length; --i >= 0; ) {
            long v = a[i];
            hash = hash * 31 + (int)(v ^ v >>> 32);
        }
        return hash == 0 ? -1 : hash;
    }

    static int hashCode(short[] a) {
        int hash = 0;
        for (int i = a.length; --i >= 0; ) {
            hash = (hash << 1) + a[i];
        }
        return hash == 0 ? -1 : hash;
    }

    static int hashCode(Object[] a) {
        int hash = 0;
        for (int i = a.length; --i >= 0; ) {
            hash = hash * 31 + hashCode(a[i]);
        }
        return hash == 0 ? -1 : hash;
    }

    // Compute object or array hashcode and recurses into arrays within.
    static int hashCode(Object a) {
        if (a == null) {
            return -1;
        }
        if (!a.getClass().isArray()) {
            return a.hashCode();
        }
        if (a instanceof Object[]) {
            return hashCode((Object[])a);
        } else if (a instanceof int[]) {
            return hashCode((int[])a);
        } else if (a instanceof float[]) {
            return hashCode((float[])a);
        } else if (a instanceof long[]) {
            return hashCode((long[])a);
        } else if (a instanceof double[]) {
            return hashCode((double[])a);
        } else if (a instanceof byte[]) {
            return hashCode((byte[])a);
        } else if (a instanceof char[]) {
            return hashCode((char[])a);
        } else if (a instanceof boolean[]) {
            return hashCode((boolean[])a);
        } else if (a instanceof short[]) {
            return hashCode((short[])a);
        } else {
            int hash = a.getClass().hashCode();
            return hash == 0 ? -1 : hash;
        }
    }

    // Compares object arrays and recurses into arrays within.
    static boolean equals(Object[] a, Object[] b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        int i;
        if ((i = a.length) != b.length) {
            return false;
        }
        while (--i >= 0) {
            if (!equals(a[i], b[i])) {
                return false;
            }
        }
        return true;
    }

    // Compares objects or arrays and recurses into arrays within.
    static boolean equals(Object a, Object b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        Class ac = a.getClass();
        if (!(ac.isArray())) {
            return a.equals(b);
        }
        if (ac != b.getClass()) {
            return false;
        }
        if (a instanceof Object[]) {
            return equals((Object[])a, (Object[])b);
        } else if (a instanceof int[]) {
            return Arrays.equals((int[])a, (int[])b);
        } else if (a instanceof float[]) {
            return Arrays.equals((float[])a, (float[])b);
        } else if (a instanceof long[]) {
            return Arrays.equals((long[])a, (long[])b);
        } else if (a instanceof double[]) {
            return Arrays.equals((double[])a, (double[])b);
        } else if (a instanceof byte[]) {
            return Arrays.equals((byte[])a, (byte[])b);
        } else if (a instanceof char[]) {
            return Arrays.equals((char[])a, (char[])b);
        } else if (a instanceof boolean[]) {
            return Arrays.equals((boolean[])a, (boolean[])b);
        } else if (a instanceof short[]) {
            return Arrays.equals((short[])a, (short[])b);
        } else {
            return a.equals(b);
        }
    }

    static int compare(boolean[] a, boolean[] b) {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return 1;
        }
        if (b == null) {
            return -1;
        }
        int length = Math.min(a.length, b.length);
        for (int i=0; i<length; i++) {
            int av = a[i] ? 0 : 1;
            int bv = b[i] ? 0 : 1;
            return av < bv ? -1 : (av > bv ? 1 : 0);
        }
        return a.length < b.length ? -1 : (a.length > b.length ? 1 : 0);
    }

    static int compare(byte[] a, byte[] b) {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return 1;
        }
        if (b == null) {
            return -1;
        }
        int length = Math.min(a.length, b.length);
        for (int i=0; i<length; i++) {
            byte av = a[i];
            byte bv = b[i];
            return av < bv ? -1 : (av > bv ? 1 : 0);
        }
        return a.length < b.length ? -1 : (a.length > b.length ? 1 : 0);
    }

    static int compare(char[] a, char[] b) {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return 1;
        }
        if (b == null) {
            return -1;
        }
        int length = Math.min(a.length, b.length);
        for (int i=0; i<length; i++) {
            char av = a[i];
            char bv = b[i];
            return av < bv ? -1 : (av > bv ? 1 : 0);
        }
        return a.length < b.length ? -1 : (a.length > b.length ? 1 : 0);
    }

    static int compare(double[] a, double[] b) {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return 1;
        }
        if (b == null) {
            return -1;
        }
        int length = Math.min(a.length, b.length);
        for (int i=0; i<length; i++) {
            int v = Double.compare(a[i], b[i]);
            if (v != 0) {
                return v;
            }
        }
        return a.length < b.length ? -1 : (a.length > b.length ? 1 : 0);
    }

    static int compare(float[] a, float[] b) {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return 1;
        }
        if (b == null) {
            return -1;
        }
        int length = Math.min(a.length, b.length);
        for (int i=0; i<length; i++) {
            int v = Float.compare(a[i], b[i]);
            if (v != 0) {
                return v;
            }
        }
        return a.length < b.length ? -1 : (a.length > b.length ? 1 : 0);
    }

    static int compare(int[] a, int[] b) {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return 1;
        }
        if (b == null) {
            return -1;
        }
        int length = Math.min(a.length, b.length);
        for (int i=0; i<length; i++) {
            int av = a[i];
            int bv = b[i];
            return av < bv ? -1 : (av > bv ? 1 : 0);
        }
        return a.length < b.length ? -1 : (a.length > b.length ? 1 : 0);
    }

    static int compare(long[] a, long[] b) {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return 1;
        }
        if (b == null) {
            return -1;
        }
        int length = Math.min(a.length, b.length);
        for (int i=0; i<length; i++) {
            long av = a[i];
            long bv = b[i];
            return av < bv ? -1 : (av > bv ? 1 : 0);
        }
        return a.length < b.length ? -1 : (a.length > b.length ? 1 : 0);
    }

    static int compare(short[] a, short[] b) {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return 1;
        }
        if (b == null) {
            return -1;
        }
        int length = Math.min(a.length, b.length);
        for (int i=0; i<length; i++) {
            short av = a[i];
            short bv = b[i];
            return av < bv ? -1 : (av > bv ? 1 : 0);
        }
        return a.length < b.length ? -1 : (a.length > b.length ? 1 : 0);
    }

    // Compares object arrays and recurses into arrays within.
    static int compare(Object[] a, Object[] b) {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return 1;
        }
        if (b == null) {
            return -1;
        }
        int length = Math.min(a.length, b.length);
        for (int i=0; i<length; i++) {
            int v = compare(a[i], b[i]);
            if (v != 0) {
                return v;
            }
        }
        return a.length < b.length ? -1 : (a.length > b.length ? 1 : 0);
    }

    // Compares objects or arrays and recurses into arrays within.
    static int compare(Object a, Object b) {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return 1;
        }
        if (b == null) {
            return -1;
        }
        Class ac = a.getClass();
        if (!(ac.isArray())) {
            return ((Comparable)a).compareTo(b);
        }
        if (ac != b.getClass()) {
            throw new ClassCastException();
        }
        if (a instanceof Object[]) {
            return compare((Object[])a, (Object[])b);
        } else if (a instanceof int[]) {
            return compare((int[])a, (int[])b);
        } else if (a instanceof float[]) {
            return compare((float[])a, (float[])b);
        } else if (a instanceof long[]) {
            return compare((long[])a, (long[])b);
        } else if (a instanceof double[]) {
            return compare((double[])a, (double[])b);
        } else if (a instanceof byte[]) {
            return compare((byte[])a, (byte[])b);
        } else if (a instanceof char[]) {
            return compare((char[])a, (char[])b);
        } else if (a instanceof boolean[]) {
            return compare((boolean[])a, (boolean[])b);
        } else if (a instanceof short[]) {
            return compare((short[])a, (short[])b);
        } else {
            throw new ClassCastException();
        }
    }

    protected KeyFactory() {
    }

    private static interface ArrayKey extends Comparable, java.io.Serializable {
        int hashCode();

        boolean equals(Object obj);

        int compareTo(Object obj);
    }

    private static class BooleanArrayKey implements ArrayKey {
        protected final boolean[] mArray;
        private transient int mHash;

        BooleanArrayKey(boolean[] array) {
            mArray = array;
        }

        public int hashCode() {
            int hash = mHash;
            return hash == 0 ? mHash = KeyFactory.hashCode(mArray) : hash;
        }

        public boolean equals(Object obj) {
            return this == obj ? true :
                (obj instanceof BooleanArrayKey ?
                 Arrays.equals(mArray, ((BooleanArrayKey) obj).mArray) : false);
        }

        public int compareTo(Object obj) {
            return compare(mArray, ((BooleanArrayKey) obj).mArray);
        }
    }

    private static class ByteArrayKey implements ArrayKey {
        protected final byte[] mArray;
        private transient int mHash;

        ByteArrayKey(byte[] array) {
            mArray = array;
        }

        public int hashCode() {
            int hash = mHash;
            return hash == 0 ? mHash = KeyFactory.hashCode(mArray) : hash;
        }

        public boolean equals(Object obj) {
            return this == obj ? true :
                (obj instanceof ByteArrayKey ?
                 Arrays.equals(mArray, ((ByteArrayKey) obj).mArray) : false);
        }

        public int compareTo(Object obj) {
            return compare(mArray, ((ByteArrayKey) obj).mArray);
        }
    }

    private static class CharArrayKey implements ArrayKey {
        protected final char[] mArray;
        private transient int mHash;

        CharArrayKey(char[] array) {
            mArray = array;
        }

        public int hashCode() {
            int hash = mHash;
            return hash == 0 ? mHash = KeyFactory.hashCode(mArray) : hash;
        }

        public boolean equals(Object obj) {
            return this == obj ? true :
                (obj instanceof CharArrayKey ?
                 Arrays.equals(mArray, ((CharArrayKey) obj).mArray) : false);
        }

        public int compareTo(Object obj) {
            return compare(mArray, ((CharArrayKey) obj).mArray);
        }
    }

    private static class DoubleArrayKey implements ArrayKey {
        protected final double[] mArray;
        private transient int mHash;

        DoubleArrayKey(double[] array) {
            mArray = array;
        }

        public int hashCode() {
            int hash = mHash;
            return hash == 0 ? mHash = KeyFactory.hashCode(mArray) : hash;
        }

        public boolean equals(Object obj) {
            return this == obj ? true :
                (obj instanceof DoubleArrayKey ?
                 Arrays.equals(mArray, ((DoubleArrayKey) obj).mArray) : false);
        }

        public int compareTo(Object obj) {
            return compare(mArray, ((DoubleArrayKey) obj).mArray);
        }
    }

    private static class FloatArrayKey implements ArrayKey {
        protected final float[] mArray;
        private transient int mHash;

        FloatArrayKey(float[] array) {
            mArray = array;
        }

        public int hashCode() {
            int hash = mHash;
            return hash == 0 ? mHash = KeyFactory.hashCode(mArray) : hash;
        }

        public boolean equals(Object obj) {
            return this == obj ? true :
                (obj instanceof FloatArrayKey ?
                 Arrays.equals(mArray, ((FloatArrayKey) obj).mArray) : false);
        }

        public int compareTo(Object obj) {
            return compare(mArray, ((FloatArrayKey) obj).mArray);
        }
    }

    private static class IntArrayKey implements ArrayKey {
        protected final int[] mArray;
        private transient int mHash;

        IntArrayKey(int[] array) {
            mArray = array;
        }

        public int hashCode() {
            int hash = mHash;
            return hash == 0 ? mHash = KeyFactory.hashCode(mArray) : hash;
        }

        public boolean equals(Object obj) {
            return this == obj ? true :
                (obj instanceof IntArrayKey ?
                 Arrays.equals(mArray, ((IntArrayKey) obj).mArray) : false);
        }

        public int compareTo(Object obj) {
            return compare(mArray, ((IntArrayKey) obj).mArray);
        }
    }

    private static class LongArrayKey implements ArrayKey {
        protected final long[] mArray;
        private transient int mHash;

        LongArrayKey(long[] array) {
            mArray = array;
        }

        public int hashCode() {
            int hash = mHash;
            return hash == 0 ? mHash = KeyFactory.hashCode(mArray) : hash;
        }

        public boolean equals(Object obj) {
            return this == obj ? true :
                (obj instanceof LongArrayKey ?
                 Arrays.equals(mArray, ((LongArrayKey) obj).mArray) : false);
        }

        public int compareTo(Object obj) {
            return compare(mArray, ((LongArrayKey) obj).mArray);
        }
    }

    private static class ShortArrayKey implements ArrayKey {
        protected final short[] mArray;
        private transient int mHash;

        ShortArrayKey(short[] array) {
            mArray = array;
        }

        public int hashCode() {
            int hash = mHash;
            return hash == 0 ? mHash = KeyFactory.hashCode(mArray) : hash;
        }

        public boolean equals(Object obj) {
            return this == obj ? true :
                (obj instanceof ShortArrayKey ?
                 Arrays.equals(mArray, ((ShortArrayKey) obj).mArray) : false);
        }

        public int compareTo(Object obj) {
            return compare(mArray, ((ShortArrayKey) obj).mArray);
        }
    }

    private static class ObjectArrayKey implements ArrayKey {
        protected final Object[] mArray;
        private transient int mHash;

        ObjectArrayKey(Object[] array) {
            mArray = array;
        }

        public int hashCode() {
            int hash = mHash;
            return hash == 0 ? mHash = KeyFactory.hashCode(mArray) : hash;
        }

        public boolean equals(Object obj) {
            return this == obj ? true :
                (obj instanceof ObjectArrayKey ?
                 KeyFactory.equals(mArray, ((ObjectArrayKey) obj).mArray) : false);
        }

        public int compareTo(Object obj) {
            return compare(mArray, ((ObjectArrayKey) obj).mArray);
        }
    }
}
