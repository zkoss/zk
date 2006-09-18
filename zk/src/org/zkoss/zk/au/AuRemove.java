/* AuRemove.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 11:23:40     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.au;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.Page;

/**
 * A response to remove the specified component at the client.
 * <p>data[0]: the uuid of the component being removed
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class AuRemove extends AuResponse {
	public AuRemove(Component comp) {
		super("rm", comp, comp.getUuid());
	}
	public AuRemove(Page page) {
		super("rm", page, page.getId());
	}
	/** Removes a component by its UUID.
	 * This constructor is used only if a component's UUID is changed.
	 */
	public AuRemove(String uuid) {
		super("rm", uuid);
	}
}
