/* AuInit.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 12 23:23:20     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.au;

import com.potix.zk.ui.Component;
import com.potix.zk.au.AuResponse;

/**
 * A response to initial (or re-init) the specified component at the client.
 * <p>data[0]: component's UUID
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class AuInit extends AuResponse {
	public AuInit(Component comp) {
		super("init", comp, comp.getUuid());
	}
}
