/* Generics.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep 14 18:03:59 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.lang;

import java.util.*;

/**
 * Utilities to handle generic types, such as converting a non-type object
 * to a generic type without warning.
 *
 * @author tomyeh
 * @since 6.0.0
 */
public class Generics {
	/** Returns a type-safe generic class of the given type-less class.
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	public static final <T> Class<T> cast(Class cls) {
		return (Class<T>)cls;
	}

	/** Returns a type-safe generic iterator of the given un-typed iterator.
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	public static final <T> Iterator<T> cast(Iterator it) {
		return it;
	}
	/** Returns a type-safe generic list iterator of the given un-typed list iterator.
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	public static final <T> ListIterator<T> cast(ListIterator it) {
		return it;
	}
	/** Returns a type-safe generic enumeration of the given un-typed enumeration.
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	public static final <T> Enumeration<T> cast(Enumeration en) {
		return en;
	}

	/** Returns a type-safe generic collection of the given un-typed collection.
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	public static final <T> Collection<T> cast(Collection col) {
		return col;
	}
	/** Returns a type-safe generic list of the given un-typed list.
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	public static final <T> List<T> cast(List list) {
		return list;
	}
	/** Returns a type-safe generic set of the given un-typed set.
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	public static final <T> Set<T> cast(Set set) {
		return set;
	}
	/** Returns a type-safe generic map of the given un-typed map.
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	public static final <K, V> Map<K, V> cast(Map map) {
		return map;
	}
}
