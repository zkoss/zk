/* PageSizeData.java

	Purpose:

	Description:

	History:
		11:53 AM 2022/2/9, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.action.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.zkoss.zephyr.zpr.IMeshElement;

/**
 * Used to notify the paging size has been changed
 * when the autopaging ({@link org.zkoss.zephyr.zpr.IMeshElement#withAutopaging}) is enabled
 * and user changed the size of the content.
 *
 * @author jumperchen
 */
public class PageSizeData implements ActionData {

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private int size;

	/** Returns the page size.
	 * <p>It is the same as {@link IMeshElement#getPagingChild()}'s
	 * {@link org.zkoss.zephyr.zpr.IPaging#getPageSize}.
	 */
	public int getPageSize() {
		return size;
	}
}
