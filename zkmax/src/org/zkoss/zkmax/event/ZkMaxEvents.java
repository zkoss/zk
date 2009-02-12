/* ZkMaxEvents.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug 12 11:37:25 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.event;

/**
 * Utilities to access events for ZkMax.
 *
 * @author jumperchen
 * @since 3.5.0
 */
public class ZkMaxEvents {
	private ZkMaxEvents() {} //prevent from creation

	/** The onColSize event used with {@link PortalMoveEvent}.
	 */
	public static final String ON_PORTAL_MOVE = "onPortalMove";
}
