/* ColsSizeEvent.java

	Purpose:
		
	Description:
		
	History:
		Fri Febc 11 16:09:25     2011, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.event;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.MouseEvent;

/**
 * Used to notify that all the width of all column are fixed.
 *
 * <p>When an user drags the border of a sizable column, the width of all
 * column columns are fixed and hflex(if any) of all columns are disabled.
 *
 * <p>The event is sent to the parent (e.g., {@link org.zkoss.zul.Columns}
 * and {@link org.zkoss.zul.Treecols}).
 * 
 * @author henrichen
 * @since 5.0.6
 */
public class ColsSizeEvent extends Event {
	private final Component _col;
	private final int _icol, _keys;
	private final String[] _width;
	private final String[] _oldWd;

	/** Indicates whether the Alt key is pressed.
	 * It might be returned as part of {@link #getKeys}.
	 */
	public static final int ALT_KEY = MouseEvent.ALT_KEY;
	/** Indicates whether the Ctrl key is pressed.
	 * It might be returned as part of {@link #getKeys}.
	 */
	public static final int CTRL_KEY = MouseEvent.CTRL_KEY;
	/** Indicates whether the Shift key is pressed.
	 * It might be returned as part of {@link #getKeys}.
	 */
	public static final int SHIFT_KEY = MouseEvent.SHIFT_KEY;

	/** Converts an AU request to a size event.
	 */
	public static final ColsSizeEvent getColsSizeEvent(AuRequest request) {
		final Map data = request.getData();
		final List wdlist = (List) data.get("widths");
		return new ColsSizeEvent(request.getCommand(), request.getComponent(),
			AuRequests.getInt(data, "index", 0),
			request.getDesktop().getComponentByUuid((String)data.get("column")),
			(String[]) wdlist.toArray(new String[wdlist.size()]), AuRequests.parseKeys(data));
	}
	
	/**
	 * Constructs an instance of {@link ColsSizeEvent}.
	 * @param icol the index of the column whose width is changed and trigger this event.
	 * @param col the component of the column
	 * @param width the width of the column
	 */
	public ColsSizeEvent(String evtnm, Component target, int icol,
	Component col, String[] width, int keys) {
		super(evtnm, target);
		_icol = icol;
		_col = col;
		_width = width;
		_oldWd = new String[_width.length];
		int j = 0;
		for(Iterator it = target.getChildren().iterator(); it.hasNext(); ++j) {
			final Object header = it.next();
			_oldWd[j] = header instanceof HtmlBasedComponent ?
					((HtmlBasedComponent)header).getWidth(): null;
		}
		_keys = keys;
	}
	/**
	 * Returns the column width
	 * @since 5.0.0
	 */
	public String getWidth(int j) {
		return _width[j];
	}
	/**
	 * Returns the previous column width
	 * @since 5.0.4
	 */
	public String getPreviousWidth(int j) {
		return _oldWd[j];
	}
	/** Return the column index whose width is changed.
	 * <p>In other words, it is the index (starting from 0) of {@link #getColumn}.
	 */
	public int getColIndex() {
		return _icol;
	}
	/** Returns the column whose width is changed.
	 * @since 3.0.0
	 */
	public Component getColumn() {
		return _col;
	}

	/** Returns what keys were pressed when the column is resized, or 0 if
	 * none of them was pressed.
	 * It is a combination of {@link #CTRL_KEY}, {@link #SHIFT_KEY}
	 * and {@link #ALT_KEY}.
	 */
	public final int getKeys() {
		return _keys;
	}
}
