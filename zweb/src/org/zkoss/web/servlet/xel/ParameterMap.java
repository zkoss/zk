/* ParameterMap.java

	Purpose:
		
	Description:
		
	History:
		Mon Sep 21 13:50:27     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.web.servlet.xel;

import java.util.AbstractSet;
import java.util.Set;
import java.util.Iterator;
import java.util.Enumeration;

import javax.servlet.ServletRequest;

/**
 * Represents a parameter map.
 * @author tomyeh
 * @since 5.0.0
 */
public class ParameterMap extends StringKeysMap {
	private final ServletRequest _request;
	private Set _entries;

	public ParameterMap(ServletRequest request) {
		_request = request;
	}

	//Map//
	public Set entrySet() {
		if (_entries == null) {
			_entries = new AbstractSet() {
				public int size() {
					return ParameterMap.this.size();
				}
				public boolean contains(Object o) {
					return ParameterMap.this.containsKey(o);
				}
				public Iterator iterator() {
					return new EntryIter();
				}
			};
		}
		return _entries;
	}

	public int size() {
		return _request.getParameterMap().size();
	}
	public boolean containsKey(Object key) {
		return _request.getParameterMap().containsKey(key);
	}

	protected Object getValue(String key) {
		return _request.getParameter(key);
	}
	protected Enumeration getKeys() {
		return _request.getParameterNames();
	}
	protected void setValue(String key, Object value) {
		throw new UnsupportedOperationException("readonly");
	}
	protected void removeValue(String key) {
		throw new UnsupportedOperationException("readonly");
	}
}
