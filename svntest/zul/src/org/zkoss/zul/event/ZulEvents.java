/* ZulEvents.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 18 09:20:23     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul.event;

/**
 * Utilities to access events for ZK and ZUL.
 *
 * @author tomyeh
 */
public class ZulEvents {
	private ZulEvents() {} //prevent from creation

	/** The onPaging event used with {@link PagingEvent}.
	 */
	public static final String ON_PAGING = "onPaging";
	/** The onPageSize event used with {@link PageSizeEvent}.
	 */
	public static final String ON_PAGE_SIZE = "onPageSize";
	/** The onColSize event used with {@link ColSizeEvent}.
	 */
	public static final String ON_COL_SIZE = "onColSize";
}
