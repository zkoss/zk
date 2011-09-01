/* SortEvent.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Aug 4, 2011 9:15:59 AM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import java.util.Map;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zk.ui.Component;

/**
 * Represents an event that indicates a sorting request to data.
 * @author simonpai
 * @since 5.0.8
 */
public class SortEvent extends Event {
	
	private final boolean _ascending;
	
	/**
	 * Converts an AU request to a sort event.
	 */
	public static SortEvent getSortEvent(AuRequest request) {
		final Map data = request.getData();
		return new SortEvent(request.getCommand(), request.getComponent(),
			AuRequests.getBoolean(data, ""));
	}
	
	/**
	 * Constructs a sort event.
	 * @param ascending whether it is ascending.
	 */
	public SortEvent(String name, Component target, boolean ascending) {
		super(name, target);
		_ascending = ascending;
	}
	
	/**
	 * Returns true if the sorting request is ascending.
	 */
	public final boolean isAscending() {
		return _ascending;
	}
	
}
