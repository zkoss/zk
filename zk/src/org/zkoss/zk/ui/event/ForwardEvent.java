/* ForwardEvent.java

	Purpose:
		
	Description:
		
	History:
		Sun Aug 19 13:38:09     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import org.zkoss.zk.ui.Component;

/**
 * Represents the event forwarded by the forward condition.
 * The forward condition is added by use of {@link Component#addForward}.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class ForwardEvent extends Event {
	private final Event _origin;
	/** Constructs a foward event.
	 *
	 * @param origin the original event that causes this forward event.
	 */
	public ForwardEvent(String name, Component target, Event origin) {
		super(name, target);
		_origin = origin;
	}
	/** Constructs a foward event.
	 *
	 * @param origin the original event that causes this forward event.
	 * @param data the event data returned by {@link #getData}
	 * @since 3.0.6
	 */
	public ForwardEvent(String name, Component target, Event origin,
	Object data) {
		super(name, target, data);
		_origin = origin;
	}
	/** Returns the original event.
	 */
	public Event getOrigin() {
		return _origin;
	}
}
