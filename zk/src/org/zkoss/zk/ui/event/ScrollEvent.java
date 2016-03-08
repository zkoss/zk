/* ScrollEvent.java

	Purpose:
		
	Description:
		
	History:
		Sun Oct  2 21:30:36     2005, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import java.util.Map;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zk.ui.Component;

/**
 * Represents an event caused by that user is scrolling or
 * has scrolled at the client.
 * 
 * @author tomyeh
 */
public class ScrollEvent extends Event {
	private final int _pos;
	private final double _dPos;
	private boolean _outBound;

	/** Converts an AU request to a scroll event.
	 * @since 5.0.0
	 */
	public static final ScrollEvent getScrollEvent(AuRequest request) {
		final Map<String, Object> data = request.getData();
		Object decimal = data.get("decimal");
		int pos;
		double dPos = 0;

		if (decimal != null) {
			dPos = AuRequests.getDouble(data, "decimal", 0);
			pos = (int) dPos;
		} else {
			pos = AuRequests.getInt(data, "", 0);
			dPos = pos;
		}
		Object outBound = data.get("outBound");
		if (outBound != null)
			return new ScrollEvent(request.getCommand(), request.getComponent(), dPos, (Boolean) outBound);
		else
			return new ScrollEvent(request.getCommand(), request.getComponent(), dPos);
	}

	/** Constructs an scroll-relevant event.
	 * @param pos the new position
	 */
	public ScrollEvent(String name, Component target, int pos) {
		super(name, target);
		_pos = pos;
		_dPos = pos;
	}

	/** Constructs an scroll-relevant event.
	 * @param pos the new position
	 * @since 7.0.1
	 */
	public ScrollEvent(String name, Component target, double pos) {
		super(name, target);
		_pos = (int) pos;
		_dPos = pos;
	}

	/** Constructs an scroll-relevant event.
	 * @param pos the new position
	 * @param outBound the position is outside the boundary or not (only used on tablet/mobile device)
	 */
	public ScrollEvent(String name, Component target, int pos, boolean outBound) {
		super(name, target);
		_pos = pos;
		_dPos = pos;
		_outBound = outBound;
	}

	/** Constructs an scroll-relevant event.
	 * @param dPos the new position
	 * @param outBound the position is outside the boundary or not (only used on tablet/mobile device)
	 * @since 7.0.1
	 */
	public ScrollEvent(String name, Component target, double dPos, boolean outBound) {
		super(name, target);
		_pos = (int) dPos;
		_dPos = dPos;
		_outBound = outBound;
	}

	/** Returns the position.
	 */
	public final int getPos() {
		return _pos;
	}

	/** Returns the position.
	 * @since 7.0.1
	 */
	public final double getPosInDouble() {
		return _dPos;
	}

	/** Return true if the scroll position is outside of boundary on tablet/mobile device.
	 * @since 6.5.0
	 */
	public boolean isOutOfBound() {
		return _outBound;
	}
}
