/* VisibilityChangeEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2012/11/21 PM 5:22:32 , Created by Vincent
}}IS_NOTE

Copyright (C) 2012 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zk.ui.event;

import java.util.Map;

import org.zkoss.zk.au.AuRequest;

/**
 * The VisibilityChangeEvent is used to notify current page/tab's visibility
 * state. Only worked if the browser support <a
 * href="http://www.w3.org/TR/page-visibility/">HTML 5 Page Visibility API</a>.
 * 
 * <p>
 * This event is sent if and only if it is registered to a root component.
 * 
 * @author Vincent
 * @since 6.5.1
 */
public class VisibilityChangeEvent extends Event {

	private static final long serialVersionUID = 20121121172232L;
	private final boolean _hidden;
	private final String _visibleState;

	/**
	 * Constructs an event to hold the current page's visibility state.
	 * 
	 * <p>Note: {@link #getTarget} will return null. It means it is a broadcast
	 * event.
	 */
	public VisibilityChangeEvent(String name, boolean hidden,
			String visibleState) {
		super(name, null);
		_hidden = hidden;
		_visibleState = visibleState;
	}

	/**
	 * Converts an AU request to a visibility change event.
	 */
	public static final VisibilityChangeEvent getVisibilityChangeEvent(
			AuRequest request) {
		Map<String, Object> data = request.getData();
		return new VisibilityChangeEvent(request.getCommand(),
				(Boolean) data.get("hidden"),
				(String) data.get("visibilityState"));
	}

	/**
	 * Return the current page's visibility state.
	 * <p>
	 * Refer to <a href="http://www.w3.org/TR/page-visibility/">HTML 5 Page
	 * Visibility API</a> for more information.
	 */
	public String getVisibilityState() {
		return _visibleState;
	}

	/**
	 * Return the current page is hidden or not.
	 * <p>
	 * Refer to <a href="http://www.w3.org/TR/page-visibility/">HTML 5 Page
	 * Visibility API</a> for more information.
	 */
	public boolean isHidden() {
		return _hidden;
	}
}
