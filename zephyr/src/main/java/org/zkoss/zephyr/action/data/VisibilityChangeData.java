/* VisibilityChangeData.java

	Purpose:
		
	Description:
		
	History:
		4:16 PM 2022/8/12, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.action.data;

/**
 * The VisibilityChangeAction is used to notify current page/tab's visibility
 * state. Only worked if the browser support <a
 * href="http://www.w3.org/TR/page-visibility/">HTML 5 Page Visibility API</a>.
 *
 * <p>
 * This action is sent if and only if it is registered to a root component.
 *
 * @author jumperchen
 */
public class VisibilityChangeData {
	private boolean hidden;
	private String visibleState;

	/**
	 * Return the current page's visibility state.
	 * <p>
	 * Refer to <a href="http://www.w3.org/TR/page-visibility/">HTML 5 Page
	 * Visibility API</a> for more information.
	 */
	public String getVisibilityState() {
		return visibleState;
	}

	/**
	 * Return the current page is hidden or not.
	 * <p>
	 * Refer to <a href="http://www.w3.org/TR/page-visibility/">HTML 5 Page
	 * Visibility API</a> for more information.
	 */
	public boolean isHidden() {
		return hidden;
	}
}
