/* SwipeData.java

	Purpose:

	Description:

	History:
		9:59 AM 2021/12/28, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.action.data;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an action that indicates a finger swipe on a component and provides
 * information about the swipe displacement, duration and direction.
 * @author jumperchen
 */
public class SwipeData implements ActionData {

	@JsonProperty(value = "dir", access = JsonProperty.Access.WRITE_ONLY)
	private String swipeDirection;

	@JsonProperty(value = "dispX", access = JsonProperty.Access.WRITE_ONLY)
	private int swipeX;

	@JsonProperty(value = "dispY", access = JsonProperty.Access.WRITE_ONLY)
	private int swipeY;

	@JsonProperty(value = "dispT", access = JsonProperty.Access.WRITE_ONLY)
	private int swipeDuration;

	/**
	 * Returns the horizontal swipe displacement relevant to the component.
	 */
	public int getSwipeX() {
		return swipeX;
	}

	/**
	 * Returns the vertical swipe displacement relevant to the component.
	 */
	public int getSwipeY() {
		return swipeY;
	}

	/**
	 * Returns the swipe duration(milliseconds) relevant to the component.
	 */
	public int getSwipeDuration() {
		return swipeDuration;
	}

	/**
	 * Returns the swipe direction(left/right/up/down) relevant to the
	 * component.
	 */
	public String getSwipeDirection() {
		return swipeDirection;
	}

	/**
	 * @hidden for Javadoc
	 */
	@Override
	public String toString() {
		return "SwipeData{" + "swipeDirection='" + swipeDirection + '\'' + ", swipeX=" + swipeX
				+ ", swipeY=" + swipeY + ", swipeDuration=" + swipeDuration + '}';
	}
}
