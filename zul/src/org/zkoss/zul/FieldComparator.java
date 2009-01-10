/* FieldComparator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jan 8, 2009 5:49:21 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.zul;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import org.zkoss.lang.Strings;
import org.zkoss.lang.reflect.Fields;
import org.zkoss.util.CollectionsX;
import org.zkoss.zk.ui.UiException;

/**
 * <p>Based on the the given field names to compare the field value of the object 
 * that is passed in {@link #compare} method.</p> 
 * <p>The field names accept compound "a.b.c" expression. It also
 * accept multiple field names that you can give expression in the form 
 * of e.g. "name, age, salary" and this comparator will compare in that sequence.</p>
 * 
 * @author henrichen
 * @since 3.0.9
 */
public class FieldComparator implements Comparator {
	/** The field names collection. */
	private Collection _fieldnames;
	/** Ascending. */
	private final boolean _asc;
	/** Ignore case. */
	private boolean _igcase;
	/** Whether to treat null as the maximum value. */
	private boolean _maxnull;
	
	/** Compares with the fields per the given field names.
	 * <p>Note: It assumes the case-insensitive and deem null as minimum value.
	 *  If not, use {@link #FieldComparator(String, boolean, boolean, boolean)}
	 * instead.</p>
	 * 
	 * @param fieldnames the fields to be compared upon for the given object in {@link #compare}.
	 * @param ascending whether to sort as ascending (or descending).
	 */
	public FieldComparator(String fieldnames, boolean ascending) {
		this(fieldnames, ascending, true, false);
	}
	
	/** Compares with the fields per the given field names.
	 *
	 * @param fieldnames the fields to be compared upon for the given object in {@link #compare}.
	 * @param ascending whether to sort as ascending (or descending).
	 * @param ignoreCase whether to sort case-insensitive
	 * @param nullAsMax whether to consider null as the maximum value.
	 * If false, null is considered as the minimum value.
	 */
	public FieldComparator(String fieldnames, boolean ascending,
			boolean ignoreCase, boolean nullAsMax) {
		if (Strings.isBlank(fieldnames)) {
			throw new UiException("Empty fieldnames: "+ fieldnames);
		}
		_fieldnames = CollectionsX.parse(new ArrayList(), fieldnames, ',');
		_asc = ascending;
		_igcase = ignoreCase;
		_maxnull = nullAsMax;
	}
	
	public int compare(Object o1, Object o2) {
		try {
			for(final Iterator it = _fieldnames.iterator(); it.hasNext();) {
				final String fieldname = ((String) it.next()).trim();
				final int res = compare0(o1, o2, fieldname);
				if (res != 0) {
					return res;
				}
			}
			return 0; 
		} catch (NoSuchMethodException ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	
	private int compare0(Object o1, Object o2, String fieldname) throws NoSuchMethodException { 
		final Object f1 = Fields.getByCompound(o1, fieldname);
		final Object f2 = Fields.getByCompound(o2, fieldname);
		final Object v1 = handleCase(f1);
		final Object v2 = handleCase(f2);
		
		if (v1 == null) return v2 == null ? 0: _maxnull ? 1: -1;
		if (v2 == null) return _maxnull  ? -1: 1;
		final int v = ((Comparable)v1).compareTo(v2);
		return _asc ? v: -v;
	}

	private Object handleCase(Object c) {
		if (_igcase) {
			if (c instanceof String)
				return ((String)c).toUpperCase();
			if (c instanceof Character)
				return new Character(Character.toUpperCase(
					((Character)c).charValue()));
		}
		return c;
	}
}
