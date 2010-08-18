/* WidgetDefinitionImpl.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 16 11:06:05     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.metainfo.*;
import org.zkoss.zk.ui.UiException;

/**
 * An implementation of WidgetDefinition.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class WidgetDefinitionImpl implements WidgetDefinition {
	/** The widget class. */
	private final String _class;
	/** A map of molds (String mold, [String moldURI]). */
	private Map _molds;
	/** Whether to preserve the blank text. */
	private final boolean _blankpresv;

	public WidgetDefinitionImpl(String klass, boolean blankPreserved) {
		_class = klass;
		_blankpresv = blankPreserved;
	}

	//WidgetDefinition//
	public String getWidgetClass() {
		return _class;
	}
	public boolean isBlankPreserved() {
		return _blankpresv;
	}
	public void addMold(String name, String moldURI) {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException();

		if (_molds == null)
			_molds = new HashMap(2);
		_molds.put(name, new String[] {moldURI});
	}
	public String getMoldURI(String name) {
		if (_molds == null)
			return null;

		final String[] info = (String[])_molds.get(name);
		return info != null ? info[0]: null;
	}
	public boolean hasMold(String name) {
		return _molds != null && _molds.containsKey(name);
	}
	public Collection getMoldNames() {
		return _molds != null ?
			_molds.keySet(): (Collection)Collections.EMPTY_LIST;
	}
}
