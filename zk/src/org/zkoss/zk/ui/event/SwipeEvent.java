/* SwipeEvent.java

	Purpose:
		
	Description:
		
	History:
		Jul 25, 2012 10:02:15 AM , Created by vincentjian

Copyright (C) 2012 Potix Corporation. All Rights Reserved.

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
 * Represents an event that indicates swipe on a component and provides
 * information about the swipe displacement, duration and direction.
 * @author vincentjian
 * @since 6.5.0
 */
public class SwipeEvent extends Event {
	private int _swipeX, _swipeY, _swipeTime;
	private String _swipeDir;

	public static final SwipeEvent getSwipeEvent(AuRequest request) {
		final Map<String, Object> data = request.getData();
		return new SwipeEvent(request.getCommand(), request.getComponent(),
				AuRequests.getInt(data, "dispX", 0),
				AuRequests.getInt(data,	"dispY", 0),
				AuRequests.getInt(data, "dispT", 0),
				data.get("dir").toString());
	}

	public SwipeEvent(String name, Component target, int swipeX, int swipeY,
			int swipeTime, String swipeDir) {
		super(name, target);
		_swipeX = swipeX;
		_swipeY = swipeY;
		_swipeTime = swipeTime;
		_swipeDir = swipeDir;
	}

	/**
	 * Returns the horizontal swipe displacement relevant to the component.
	 */
	public int getSwipeX() {
		return _swipeX;
	}

	/**
	 * Returns the vertical swipe displacement relevant to the component.
	 */
	public int getSwipeY() {
		return _swipeY;
	}

	/**
	 * Returns the swipe duration(milliseconds) relevant to the component.
	 */
	public int getSwipeDuration() {
		return _swipeTime;
	}

	/**
	 * Returns the swipe direction(left/right/up/down) relevant to the
	 * component.
	 */
	public String getSwipeDirection() {
		return _swipeDir;
	}
}
