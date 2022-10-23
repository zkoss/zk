/* AfterSizeData.java

	Purpose:

	Description:

	History:
		10:14 AM 2021/12/28, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.action.data;

/**
 * Represents an action that a user resizes a component and provides
 * the new size of the component.
 * @author jumperchen
 */
public class AfterSizeData implements ActionData {
	private int width, height;

	/**
	 * @return the offsetWidth of the component after sized
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the offsetHeight of the component after sized
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @hidden for Javadoc
	 */
	public String toString() {
		return "AfterSizeData{" + "width=" + width + ", height=" + height + '}';
	}
}
