/* StateChangeData.java

	Purpose:

	Description:

	History:
		3:26 PM 2022/3/21, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.action.data;

/**
 * Represents an action that state of component was changed.
 * @author jumperchen
 */
public class StateChangeData implements ActionData {
	private int state;

	/**
	 * Returns the current state that has been changed.
	 */
	public int getState() {
		return state;
	}
}
