/* MoveData.java

	Purpose:

	Description:

	History:
		6:00 PM 2022/3/2, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.action.data;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

import org.zkoss.zk.au.AuRequests;

/**
 * Represents an action caused by a component being moved.
 * @author jumperchen
 */
public class MoveData implements ActionData {

	private final String _left, _top;
	private final int _keys;

	@JsonCreator
	private MoveData(Map data) {
		_left = (String) data.get("left");
		_top = (String) data.get("top");
		_keys = AuRequests.parseKeys(data);
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

	/** Returns the left of the component after moved.
	 */
	public final String getLeft() {
		return _left;
	}

	/** Returns the top of the component after moved.
	 */
	public final String getTop() {
		return _top;
	}

	/** Returns what keys were pressed when the component is moved, or 0 if
	 * none of them was pressed.
	 * It is a combination of {@link #CTRL_KEY}, {@link #SHIFT_KEY}
	 * and {@link #ALT_KEY}.
	 */
	public final int getKeys() {
		return _keys;
	}
}
