/* AfterSizeEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mar 13, 2013 2:46:41 PM , Created by Vincent
}}IS_NOTE

Copyright (C) 2013 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import java.util.Map;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zk.ui.Component;

/**
 * Represents an event that resize a component and provides
 * the new size of the component.
 * @author Vincent
 * @since 6.5.2
 */
public class AfterSizeEvent extends Event {
	
	private static final long serialVersionUID = 20130313144641L;
	private final int _width, _height;
	
	/** Constructs an AfterSizeEvent.
	 */
	public AfterSizeEvent(String name, Component target, int width, int height) {
		super(name, target);
		_width = width;
		_height = height;
	}
	
	/**
	 * Converts an AU request to a AfterSizeEvent.
	 */
	public static AfterSizeEvent getAfterSizeEvent(AuRequest request) {
		Map<String, Object> data = request.getData();
		return new AfterSizeEvent(request.getCommand(), request.getComponent(),
				AuRequests.getInt(data, "width" , 0),
				AuRequests.getInt(data, "height", 0));
	}
	
	/**
	 * @return the offsetWidth of the component after sized
	 */
	public int getWidth() {
		return _width;
	}
	
	/**
	 * @return the offsetHeight of the component after sized
	 */
	public int getHeight() {
		return _height;
	}
}
