/* InputEvent.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 14 17:39:00     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;

/**
 * Represents an event cause by user's input something at the client.
 * 
 * @author tomyeh
 */
public class InputEvent extends Event {
	private final String _val;
	private final boolean _selbk;
	private final int _start;

	/** Converts an AU request to an input event.
	 * @since 5.0.0
	 */
	public static final InputEvent getInputEvent(AuRequest request) {
		final Map data = request.getData();
		return new InputEvent(request.getCommand(), request.getComponent(),
			(String)data.get("value"),
			AuRequests.getBoolean(data, "bySelectBack"),
			AuRequests.getInt(data, "start", 0));
	}

	/** Constructs a input-relevant event.
	 * @param val the new value
	 */
	public InputEvent(String name, Component target, String val) {
		this(name, target, val, false, 0);
	}
	/** Constructs an event for <code>onChanging</code>.
	 *
	 * @param selbk whether this event is caused by user's selecting a list
	 * of items. Currently, only combobox might set it to true for the onChanging
	 * event. See {@link #isChangingBySelectBack} for details.
	 */
	public InputEvent(String name, Component target, String val, boolean selbk, int start) {
		super(name, target);
		_val = val;
		_selbk = selbk;
		_start = start;
	}
	/** Returns the value that user input.
	 */
	public final String getValue() {
		return _val;
	}
	/** Returns whether this event is <code>onChanging</code>, and caused by
	 * user's selecting a list of predefined values (aka., items).
	 *
	 * <p>It is always false if it is caused by the <code>onChange</code> event.
	 *
	 * <p>Currently, only combobox might set it to true for the onChanging
	 * event. It is useful when you implement autocomplete.
	 * To have better response, you usually don't filter out unmatched items
	 * if this method returns true. In other words, you simply ignore
	 * the <code>onChanging</code> event if this method return true, when
	 * implementing autocomplete.
	 */
	public final boolean isChangingBySelectBack() {
		return _selbk;
	}

	/**
	 * Returns the start position of the cursor from the input element.
	 * <p>Note: In IE browser, we cannot get the position of cursor because <code>onblur</code> 
	 * event of Javascript is always fired before <code>onChange</code> is fired. 
	 * To get the position of cursor, <code>onChanging</code> event is suggested
	 *  since onblur event of Javascript will not be fired.</p>
	 * @return the start position >= 0
	 * @since 3.0.1
	 */
	public int getStart() {
		return _start;
	}
}
