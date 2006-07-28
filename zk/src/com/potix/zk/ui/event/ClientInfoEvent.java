/* ClientInfoEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 25 16:34:05     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zk.ui.event;

import java.util.TimeZone;
import com.potix.util.TimeZones;

/**
 * The onClientInfo event is used to notify the client's information, such
 * as time zone and screen resolutions.
 *
 * <p>This event is sent if and only if it is registered to a root component.
 * In other words,
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class ClientInfoEvent extends Event {
	private final TimeZone _timeZone;
	private final int _width, _height, _colorDepth;

	/** Constructs an event to hold the client-info.
	 *
	 * <p>Note: {@link #getTarget} will return null. It means it is a broadcast
	 * event.
	 */
	public ClientInfoEvent(String name, int timeZoneOfs,
	int width, int height, int colorDepth) {
		super(name, null);
		_width = width;
		_height = height;
		_colorDepth = colorDepth;

		final StringBuffer sb = new StringBuffer(8).append("GMT");
		//Note: we have to revert the sign
		//see http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Date:getTimezoneOffset
		_timeZone = TimeZones.getTimeZone(-timeZoneOfs);
	}
	/** Returns the time zone of the client.
	 */
	public TimeZone getTimeZone() {
		return _timeZone;
	}
	/** Returns the pixel width of the client's screen.
	 */
	public int getWidth() {
		return _width;
	}
	/** Returns the pixel height of the client's screen.
	 */
	public int getHeight() {
		return _height;
	}
	/** Returns the maximum number of colors the client's screen supports.
	 */
	public int getColorDepth() {
		return _colorDepth;
	}
}
