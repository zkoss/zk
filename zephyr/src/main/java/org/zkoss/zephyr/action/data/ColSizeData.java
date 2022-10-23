/* ColSizeData.java

	Purpose:

	Description:

	History:
		11:12 AM 2022/3/10, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.action.data;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

import org.zkoss.zk.au.AuRequests;

/**
 * Used to notify that the width of a column is changed.
 *
 * <p>When a user drags the border of a sizable column, only the width of the
 * column itself is changed, other columns are not affected.
 * @author jumperchen
 */
public class ColSizeData implements ActionData {
	private final int index, keys;
	private final String width;
	private final String[] widths;

	@JsonCreator
	private ColSizeData(Map data) {
		List<String> wdlist = (List<String>) data.get("widths");
		if (wdlist != null) {
			this.widths = wdlist.toArray(new String[0]);
		} else {
			this.widths = new String[0];
		}
		this.index = AuRequests.getInt(data, "index", 0);
		this.width = (String) data.get("width");
		this.keys = AuRequests.parseKeys(data);
	}

	/** Indicates whether the Alt key is pressed.
	 * It might be returned as part of {@link #getKeys}.
	 */
	public static final int ALT_KEY = MouseData.ALT_KEY;
	/** Indicates whether the Ctrl key is pressed.
	 * It might be returned as part of {@link #getKeys}.
	 */
	public static final int CTRL_KEY = MouseData.CTRL_KEY;
	/** Indicates whether the Shift key is pressed.
	 * It might be returned as part of {@link #getKeys}.
	 */
	public static final int SHIFT_KEY = MouseData.SHIFT_KEY;

	/**
	 * Returns the column width
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * Returns the column width of the specified column index.
	 * @param col the column index
	 */
	public String getWidth(int col) {
		return widths[col];
	}

	/** Return the column index of the first column whose width is changed.
	 * The other column is the returned index plus one.
	 * <p>In other words, it is the index (starting from 0).
	 */
	public int getColIndex() {
		return index;
	}

	/** Returns what keys were pressed when the column is resized, or 0 if
	 * none of them was pressed.
	 * It is a combination of {@link #CTRL_KEY}, {@link #SHIFT_KEY}
	 * and {@link #ALT_KEY}.
	 */
	public final int getKeys() {
		return keys;
	}
}
