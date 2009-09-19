/* Event.java

	Purpose:
		
	Description:
		
	History:
		Sat Jun 11 10:41:14     2005, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import java.util.List;
import java.util.Map;
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.au.AuRequest;

/**
 * An event sent to the event handler of a component.
 *
 * @author tomyeh
 * @see Component
 */
public class Event {
	private final String _name;
	private final Component _target;
	private final Object _data;
	private boolean _propagatable = true;

	/** Creates an instance of {@link Event} based on the specified request.
	 */
	public static Event getEvent(AuRequest request) {
		final String name = request.getCommand();
		final Component comp = request.getComponent();
		final Map data = request.getData();
		final Object data2 = data != null ? data.get(""): null;
		if (data2 == null)
			return new Event(name, comp);
		if (data2 instanceof List) {
			final List ary = (List)data2;
			final Object[] data3 = new Object[ary.size()];
			int j = 0;
			for (Iterator it = ary.iterator(); it.hasNext();)
				data3[j++] = it.next();
			return new Event(name, comp, data3);
		}
		return new Event(name, comp, data2);
	}

	/** Constructs a simple event.
	 * @param target the component to receive this event,
	 * or null to indicate broadcasting the event to all root components.
	 */
	public Event(String name, Component target) {
		if (name == null)
			throw new NullPointerException();
		_name = name;
		_target = target;
		_data = null;
	}
	/** Constructs a simple event.
	 * @param target the component to receive this event,
	 * or null to indicate broadcasting the event to all root components.
	 * @param data an arbitary data
	 */
	public Event(String name, Component target, Object data) {
		if (name == null)
			throw new NullPointerException();
		_name = name;
		_target = target;
		_data = data;
	}

	/** Returns the event name.
	 */
	public final String getName() {
		return _name;
	}
	/** Returns the target component that receives this event,
	 * or null if broadcast.
	 */
	public final Component getTarget() {
		return _target;
	}
	/** Returns the page owning this event, or null if broadcast.
	 */
	public final Page getPage() {
		return _target != null ? _target.getPage(): null;
	}
	/** Returns the data accompanies with this event, or null if not available.
	 */
	public Object getData() {
		return _data;
	}

	/** Returns whether this event is propagatable.
	 * <p>Default: true.
	 * <p>It becomes false if {@link #stopPropagation} is called.
	 * If true, the event will be sent to the following event listener
	 * ({@link EventListener}) being registered by {@link Component#addEventListener}
	 * and {@link Page#addEventListener}.
	 */
	public boolean isPropagatable() {
		return _propagatable;
	}
	/** Stops the propagation for this event.
	 */
	public void stopPropagation() {
		_propagatable = false;
	}

	//-- Object --//
	public String toString() {
		final String clsnm = getClass().getName();
		final int j = clsnm.lastIndexOf('.');
		return "["+clsnm.substring(j+1)+' '+_name+' '+_target+']';
	}
}
