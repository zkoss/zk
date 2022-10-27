/* SizeData.java

	Purpose:

	Description:

	History:
		5:47 PM 2022/3/2, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.action.data;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

import org.zkoss.zk.au.AuRequests;

/**
 * Represents an action caused by a component being re-sized.
 * @author jumperchen
 */
public class SizeData implements ActionData {
	private final String _width, _height;
	private final int _keys;

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

	@JsonCreator
	private SizeData(Map data) {
		_width = (String) data.get("width");
		_height = (String) data.get("height");
		_keys = AuRequests.parseKeys(data);
	}
	/** Returns the width of the component after re-sized.
	 */
	public final String getWidth() {
		return _width;
	}

	/** Returns the height of the component after re-sized.
	 */
	public final String getHeight() {
		return _height;
	}

	/** Returns what keys were pressed when the component is resized, or 0 if
	 * none of them was pressed.
	 * It is a combination of {@link #CTRL_KEY}, {@link #SHIFT_KEY}
	 * and {@link #ALT_KEY}.
	 */
	public final int getKeys() {
		return _keys;
	}
}
