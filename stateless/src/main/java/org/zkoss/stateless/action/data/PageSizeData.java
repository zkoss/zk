/* PageSizeData.java

	Purpose:

	Description:

	History:
		11:53 AM 2022/2/9, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.action.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.zkoss.stateless.sul.IMeshElement;
import org.zkoss.stateless.sul.IPaging;

/**
 * Used to notify the paging size has been changed
 * when the autopaging ({@link IMeshElement#withAutopaging}) is enabled
 * and user changed the size of the content.
 *
 * @author jumperchen
 */
public class PageSizeData implements ActionData {

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private int size;

	/** Returns the page size.
	 * <p>It is the same as {@link IMeshElement#getPagingChild()}'s
	 * {@link IPaging#getPageSize}.
	 */
	public int getPageSize() {
		return size;
	}
}
