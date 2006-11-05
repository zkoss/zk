/* AnnotationImpl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Nov  5 12:38:46     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;

/**
 * An implementation of the annotation.
 *
 * <p>It is not thread-safe! You have to synchronize the access if multi-threading
 * is used.
 *
 * @author tomyeh
 */
public class AnnotationImpl implements Annotation {
	private final String _name;
	private Map _attrs;

	public AnnotationImpl(String name) {
		_name = name;
	}

	//Extra//
	/** Adds the specified attribute to the annotation.
	 *
	 * @param name the attribute name. "value" is assumed if name is null or empty.
	 * @param value the attribute value. If null, removal is assumed.
	 */
	public void setAttribute(String name, String value) {
		if (name == null || name.length() == 0)
			name = "value";
		if (value != null) {
			if (_attrs == null)
				_attrs = new LinkedHashMap(5);
			_attrs.put(name, value);
		} else {
			if (_attrs != null)
				_attrs.remove(name);
		}
	}

	//Annotation//
	public String getName() {
		return _name;
	}
	public Map getAttributes() {
		return _attrs != null ? Collections.unmodifiableMap(_attrs):
			Collections.EMPTY_MAP;
	}
	public String getAttribute(String name) {
		return _attrs != null ? (String)_attrs.get(name): null;
	}
	public String toString() {
		return '[' + _name + ": " + _attrs + ']';
	}
}
