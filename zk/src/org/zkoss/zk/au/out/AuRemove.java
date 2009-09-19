/* AuRemove.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 11:23:40     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.au.AuResponse;

/**
 * A response to remove the specified component at the client.
 * <p>data[0]: the uuid of the component being removed
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class AuRemove extends AuResponse {
	public AuRemove(Component comp) {
		super("rm", comp, comp.getUuid());
	}
	public AuRemove(Page page) {
		super("rm", page, page.getUuid());
	}
	/** Removes a component by its UUID.
	 * This constructor is used only if a component's UUID is changed.
	 */
	public AuRemove(String uuid) {
		super("rm", uuid);
	}
}
