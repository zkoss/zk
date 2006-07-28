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
import com.potix.zk.ui.Component;

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
	private int _width, _height;

	/** Constructs a event to hold the client-info.
	 */
	public ClientInfoEvent(String name, Component target, int width, int height,
	int timeZoneOfs) {
		super(name, target);
		_width = width;
		_height = height;
		_timeZone = null; //TODO
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
}
