/* CollectionsX.java

	Purpose:
	Description:
	History:
	2001/09/15 13:46:20, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Set;
import java.util.Map;
import java.util.AbstractList;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import org.zkoss.lang.Strings;

/**
 * The collection related utilities.
 *
 * @author tomyeh
 * @see java.util.Collections
 */
public class CollectionsX {
	/** An enumeration on top of a collection or iterator.
	 */
	public static final class CollectionEnumeration<E> implements Enumeration<E> {
		private final Iterator<? extends E> _iter;
		public CollectionEnumeration(Collection<? extends E> c) {
			this(c != null ? c.iterator(): null);
		}
		public CollectionEnumeration(Iterator<? extends E> iter) {
			_iter = iter;
		}
		public final boolean hasMoreElements() {
			return _iter != null && _iter.hasNext();
		}
		public final E nextElement() {
			if (_iter != null)
				return _iter.next();
			throw new NoSuchElementException();
		}
	}
	/** An enumeration on top of an array.
	 */
	public static final class ArrayEnumeration<E> implements Enumeration<E> {
		private final Object[] _ary;
		private int _cursor = 0;
		public ArrayEnumeration(Object[] ary) {
			_ary = ary;
		}
		public final boolean hasMoreElements() {
			return _ary != null && _cursor < _ary.length;
		}
		@SuppressWarnings("unchecked")
		public final E nextElement() {
			if (hasMoreElements())
				return (E)_ary[_cursor++];
			throw new NoSuchElementException();
		}
	}
	/** An enumeration that enumerates one element.
	 */
	public static final class OneEnumeration<E> implements Enumeration<E> {
		private boolean _nomore;
		private final E _one;
		public OneEnumeration(E one) {
			_one = one;
		}
		public final boolean hasMoreElements() {
			return !_nomore;
		}
		public final E nextElement() {
			if (_nomore)
				throw new NoSuchElementException();
			_nomore = true;
			return _one;
		}
	}
	/** An readonly collection on top of an array.
	 */
	public static final class ArrayCollection<E> extends AbstractCollection<E> {
		private final Object[] _ary;
		public ArrayCollection(Object[] ary) {
			_ary = ary;
		}
		public final int size() {
			return _ary != null ? _ary.length: 0;
		}
		public Iterator<E> iterator() {
			return new ArrayIterator<E>(_ary);
		}
	}
	/** An readonly list on top of an array.
	 */
	public static final class ArrayList<E> extends AbstractList<E> {
		private final Object[] _ary;
		public ArrayList(Object[] ary) {
			_ary = ary;
		}
		public final int size() {
			return _ary != null ? _ary.length: 0;
		}
		@SuppressWarnings("unchecked")
		public final E get(int index) {
			return (E)_ary[index];
		}
	}
	/** An iterator on top of an array.
	 */
	public static class ArrayIterator<E> implements Iterator<E> {
		/*package*/ final Object[] _ary;
		/*package*/ int _cursor = 0, _last = -1;
		/** @param ary an array or null. */
		public ArrayIterator(Object[] ary) {
			_ary = ary;
		}
		public final boolean hasNext() {
			return _ary != null && _cursor < _ary.length;
		}
		@SuppressWarnings("unchecked")
		public final E next() {
			if (hasNext())
				return (E)_ary[_last = _cursor++];
			throw new NoSuchElementException("cursor="+_cursor);
		}
		public final void remove() {
			throw new UnsupportedOperationException();
		}
	}
	public static class ArrayListIterator<E> extends ArrayIterator<E>
	implements ListIterator<E> {
		/** @param ary an array or null. */
		public ArrayListIterator(Object[] ary) {
			super(ary);
		}
		/** @param ary an array or null. */
		public ArrayListIterator(E[] ary, int index) {
			super(ary);
			_cursor = index;
			final int len = _ary != null ? _ary.length: 0;
			if (_cursor < 0 || _cursor > len)
				throw new IndexOutOfBoundsException("index="+index+" but len="+len);
		}
		public final boolean hasPrevious() {
			return _ary != null && _cursor > 0;
		}
		@SuppressWarnings("unchecked")
		public final E previous() {
			if (hasPrevious())
				return (E)_ary[_last = --_cursor];
			throw new NoSuchElementException("cursor="+_cursor);
		}
		public final int nextIndex() {
			return _cursor;
		}
		public final int previousIndex() {
			return _cursor - 1;
		}
		public final void set(E o) {
			if (_last < 0)
				throw new IllegalStateException("neither next nor previous have been called");
			_ary[_last] = o;
		}
		public final void add(E o) {
			throw new UnsupportedOperationException();
		}
	}
	/** A collection that contains only one element.
	 */
	public static final class OneCollection<E> extends AbstractCollection<E> {
		private final E _one;
		public OneCollection(E one) {
			_one = one;
		}
		public final int size() {
			return 1;
		}
		public Iterator<E> iterator() {
			return new OneIterator<E>(_one);
		}
	}
	/** An iterator that iterates one element.
	 */
	public static final class OneIterator<E> implements Iterator<E> {
		private boolean _nomore;
		private final E _one;
		public OneIterator(E one) {
			_one = one;
		}
		public final boolean hasNext() {
			return !_nomore;
		}
		public final E next() {
			if (_nomore)
				throw new NoSuchElementException();
			_nomore = true;
			return _one;
		}
		public final void remove() {
			throw new UnsupportedOperationException();
		}
	}
	/** An iterator that iterates thru an Enumeration.
	 */
	public static final class EnumerationIterator<E> implements Iterator<E> {
		private final Enumeration<? extends E> _enm;
		/**
		 * @param enm the enumeration. If null, it means empty.
		 */
		public EnumerationIterator(Enumeration<? extends E> enm) {
			_enm = enm;
		}
		public final boolean hasNext() {
			return _enm != null && _enm.hasMoreElements();
		}
		public final E next() {
			if (_enm == null)
				throw new NoSuchElementException();
			return _enm.nextElement();
		}
		public final void remove() {
			throw new UnsupportedOperationException();
		}
	};

	/** Empty iterator.
	 */
	public static final Iterator EMPTY_ITERATOR =
		new Iterator() {
			public final boolean hasNext() {
				return false;
			}
			public final Object next() {
				throw new NoSuchElementException();
			}
			public final void remove() {
				throw new IllegalStateException();
			}
		};
	/** Empty enumeration.
	 */
	public static final Enumeration EMPTY_ENUMERATION =
		new Enumeration() {
			public final boolean hasMoreElements() {
				return false;
			}
			public final Object nextElement() {
				throw new NoSuchElementException();
			}
		};

	/** Returns a generic empty iterator.
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	public static final <T> Iterator<T> emptyIterator() {
		return EMPTY_ITERATOR;
	}
	/** Returns a generic empty enumeration.
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	public static final <T> Enumeration<T> emptyEnumeration() {
		return EMPTY_ENUMERATION;
	}

	/** Returns an empty iterable object.
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	public static final <T> Iterable<T> emptyIterable() {
		return EMPTY_ITERABLE;
	}
	private static final Iterable EMPTY_ITERABLE = new Iterable() {
		public Iterator iterator() {
			return EMPTY_ITERATOR;
		};
	};

	/**
	 * Returns the specified range of the specified collection into a new array.
	 * The initial index of the range (<tt>from</tt>) must lie between zero
	 * and <tt>col.size</tt>, inclusive. 
	 * The final index of the range (<tt>to</tt>), which must be greater than or
	 * equal to <tt>from</tt>.
	 * <p>The returned array will be "safe" in that no references to it are
	 * maintained by this list.  (In other words, this method must allocate
	 * a new array).  The caller is thus free to modify the returned array.
	 *
	 * <p>This method acts as bridge between array-based and collection-based
	 * APIs.
	 * @param col the collection to be copied.
	 * @param from the initial index of the range to be copied, inclusive.
	 * @param to the final index of the range to be copied, exclusive.
	 * @since 3.0.6
	 */
	@SuppressWarnings("unchecked")
	public static final Object[] toArray(Collection col, int from, int to) {
		return toArray(col, new Object[0], from, to);
	}
	/**
	 * Returns the specified range of the given collection;
	 * the runtime type of the returned array is that of the specified array.
	 * @param col the collection to be copied.
	 * @param dst the array into which the elements of this list are to be stored,
	 * if it is big enough (less than <code>to - from</code>; otherwise, a new array of the same runtime type is allocated for this purpose. 
	 * Note: dst[0] will be col[from].
	 * @param from the initial index of the range to be copied, inclusive.
	 * @param to the final index of the range to be copied, exclusive.
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T[] toArray(Collection<? extends T> col, T[] dst, int from, int to) {
		int sz = col.size();
		if (to > sz) to = sz;
		if (from < 0) from = 0;

		int newLength = to - from;
		if (newLength < 0)
			throw new IllegalArgumentException(from + " > " + to);

		if (dst.length < newLength)
			dst = (T[])Array.newInstance(dst.getClass().getComponentType(), newLength);

		int i = 0, j = 0;
		for (Iterator<? extends T> it = col.iterator(); it.hasNext() && i < dst.length;) {
			if (j++ < from) {
				it.next();
				continue;
			}
			dst[i++] = it.next();
		}
		return dst;
	}

	/** Adds all elements returned by the iterator to a collection.
	 * @param iter the iterator; null is OK
	 * @return the number element being added
	 */
	public static final <T> int addAll(Collection<T> col, Iterator<? extends T> iter) {
		int cnt = 0;
		if (iter != null)
			for (; iter.hasNext(); ++cnt)
				col.add(iter.next());
		return cnt;
	}
	/** Adds all elements returned by the enumerator to a collection.
	 * @param enm the enumeration; null is OK
	 * @return the number element being added
	 */
	public static final <T> int addAll(Collection<T> col, Enumeration<? extends T> enm) {
		int cnt = 0;
		if (enm != null)
			for (; enm.hasMoreElements(); ++cnt)
				col.add(enm.nextElement());
		return cnt;
	}
	/** Adds all elements of an array to a collection.
	 * @param ary the array; null is OK
	 * @return the number element being added
	 */
	@SuppressWarnings("unchecked")
	public static final <T> int addAll(Collection<T> col, Object[] ary) {
		int cnt = 0;
		if (ary != null)
			for (; cnt < ary.length; ++cnt)
				col.add((T)ary[cnt]);
		return cnt;
	}

	/** Tests whether two sets has any intersection.
	 */
	public static final boolean isIntersected(Set<?> a, Set<?> b) {
		final int sza = a != null ? a.size(): 0;
		final int szb = b != null ? b.size(): 0;
		if (sza == 0 || szb == 0)
			return false;

		final Set large, small;
		if (sza > szb) {
			large = a;
			small = b;
		} else {
			large = b;
			small = a;
		}
		for (final Iterator it = small.iterator(); it.hasNext();)
			if (large.contains(it.next()))
				return true;
		return false;
	}
	/**
	 * Parses a string into a list.
	 * It is the same as parse(c, src, separator, true, false).
	 * Refer to {@link #parse(Collection, String, char, boolean, boolean)}
	 * for details.
	 *
	 * @exception IllegalSyntaxException if syntax errors
	 * @see Maps#parse
	 * @see #parse(Collection, String, char, boolean, boolean)
	 */
	public static final Collection<String>
	parse(Collection<String> c, final String src, char separator) {
		return parse(c, src, separator, true);
	}
	/**
	 * Parses a string into a list.
	 * It is the same as parse(c, src, separator, escBackslash, false).
	 * Refer to {@link #parse(Collection, String, char, boolean, boolean)}
	 * for details.
	 *
	 * @exception IllegalSyntaxException if syntax errors
	 * @see Maps#parse
	 * @see #parse(Collection, String, char, boolean, boolean)
	 */
	public static final
	Collection<String> parse(Collection<String> c, final String src,
	char separator, boolean escBackslash) {
		return parse(c, src, separator, escBackslash, false);
	}
	/**
	 * Parses a string into a list.
	 * To quote a string, both '\'' and '"' are OK.
	 * Whitespaces are trimmed between quotation and separators.
	 *
	 * <p>Unlike Java, quotation could spread over multiple lines.
	 *
	 * <p>Example,<br>
	 * a b , ' c d',"'f'", '1' "2", 3<br>
	 * generate a list of "a b", "c d", "'f'", "1", "2" and "3".
	 * Note: the separator between "1" and "2" is optional.
	 *
	 * <p>Note: Like Java, if the string is ending with a separator,
	 * it will be ignored.
	 *
	 * <p>Example,<br>
	 * a, , b, <br>
	 * generate a list of "a", "", "b".
	 *
	 * @param c the collection to hold the parsed results; a linked list
	 * is created if c is null.
	 * @param src the string to parse
	 * @param separator the separator, e.g., ',', '\n' or ' '.
	 * Note: if separator is ' ', it denotes any white space.
	 * @param escBackslash whether to treat '\\' specially (as escape char)
	 * @return the <code>c</code> collection if not null; or a linked list
	 * if c is null (so you can cast it to List)
	 * @param parenthesis whether to parse parenthesis in the value, {}, () and [].
	 * If true, the separator is ignored inside the parenthesis.
	 * Specify true if the value might contain EL expressions.
	 *
	 * @exception IllegalSyntaxException if syntax errors
	 * @see Maps#parse
	 * @since 3.0.6
	 */
	public static final Collection<String> parse(Collection<String> c, final String src,
	char separator, boolean escBackslash, boolean parenthesis) {
		if (c == null)
			c = new LinkedList<String>();

		final char[] seps = new char[] {separator};
		int j = 0;
		for (Strings.Result res;
		(res = Strings.nextToken(src, j, seps, escBackslash, true, parenthesis)) != null;
		j = res.next) {
			assert res.token != null;
			c.add(res.token);
		}
		return c;
	}

	/**
	 * Based on the given collection type of Object, return an iterator. The
	 * Collection type of object can be Collection, Map (return the entry), or
	 * Array.
	 */
	@SuppressWarnings("unchecked")
	public static final Iterator iterator(Object obj) {
		if (obj instanceof Object[]) {
			return new ArrayIterator((Object[])obj);
		} else if (obj instanceof Collection) {
			return ((Collection)obj).iterator();
		} else if (obj instanceof Map) {
			return ((Map)obj).entrySet().iterator();
		}
		throw new IllegalArgumentException("obj must be a Collection, a Map, or an Object array. obj: "+obj);
	}

	/** Returns an iterator that allows the caller to modify the collection
	 * directly (in addition to Iterator.remove()).
	 * In other words, the iterator will handle
	 * java.util.ConcurrentModificationException and return the items one-by-one
	 * even if collection's add, remove or other method was called
	 * (which is not allowed by a regular iterator).
	 * <p>Limitation:
	 * <ul><li>It is still not safe to modify the collection
	 * between hasNext() and next().Modify it after next(), if necessary.</li>
	 * <li>If the collection have multiple items referencing to the same object,
	 * and if we remove the item that has been iterated, the other item might
	 * be ignored. For example, if a collection has A, B, C, B, then the
	 * following code only iterates A, B, C</li>
	 * </ul>
	 * <pre><code>
	for (Iterator it = org.zkoss.util.CollectionsX.comodifiableIterator(list); it.hasNext();) {
		it.next();
		l.remove(0);
	}</code></pre>
	 * @since 5.0.6
	 */
	public static <E> Iterator<E> comodifiableIterator(Collection<E> col) {
		return new ComodifiableIterator<E, E>(col, null);
	}
	/** Returns an iterator that allows the caller to modify the collection
	 * directly (in addition to Iterator.remove()).
	 * @param converter the filter used to convert the value of each element
	 * found in the given collection to the return iterator.
	 * Ignored if null (and then F must be the same as T or extends from T).
	 * @since 6.0.0
	 */
	public static <F, T> Iterator<T> comodifiableIterator(Collection<F> col, 
			Converter<F, T> converter) {
		return new ComodifiableIterator<F, T>(col, converter);
	}
}
/*package*/ class ComodifiableIterator<F, T> implements Iterator<T> {
	private final List<F> _visited = new LinkedList<F>();
	private List<F> _lastVisited;
	private final Collection<F> _col;
	private Iterator<F> _it;
	private F _next;
	private Converter<F, T> _converter;
	private boolean _nextAvail;

	@SuppressWarnings("unchecked")
	/*package*/ ComodifiableIterator(Collection<F> col, Converter<F, T> converter) {
		_col = col;
		_it = col.iterator();
		_converter = converter != null ? converter: _identityConverter;
	}
	public boolean hasNext() {
		//Note: we cannot just check hasNext() since it does not throw
		//ConcurrentModificationException, so if _col is cleared, hasNext()
		//might still return true! Moreover, hasNext() might return false
		//while there is more
		if (_nextAvail)
			return true;
		while (!_col.isEmpty()) { //isEmpty is reliable and empty is a common case
			final F o;
			try {
				o = _it.next();
			} catch (java.util.NoSuchElementException ex) {
				return false;
			} catch (java.util.ConcurrentModificationException ex) {
				_lastVisited = new LinkedList<F>(_visited); //make a copy
				_it = _col.iterator();
				continue; //do it again
			}
			if (!removeFromLastVisited(o)) { //not visited before
				_visited.add(o);
				_next = o;
				return _nextAvail = true;
			}
		}
		return false;
	}
	public T next() {
		if (_nextAvail) {
			_nextAvail = false;
			return _converter.convert(_next);
		}
		for (;;) {
			final F o;
			try {
				o = _it.next();
			} catch (java.util.ConcurrentModificationException ex) {
				_lastVisited = new LinkedList<F>(_visited); //make a copy
				_it = _col.iterator();
				continue; //do it again
			}
			if (!removeFromLastVisited(o)) { //not visited before
				_visited.add(o);
				return _converter.convert(o);
			}
		}
	}
	private boolean removeFromLastVisited(F o) {
		if (_lastVisited != null)
			for (Iterator<F> it = _lastVisited.iterator(); it.hasNext();) {
				if (it.next() == o) { //not equals (more retricted)
					it.remove();
					return true;
				}
			}
		return false;
	}
	public void remove() {
		_it.remove();
	}

	private static final Converter _identityConverter = new Converter() {
		public Object convert(Object o) {
			return o;
		}
	};
}
