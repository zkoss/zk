/* AuRedraw.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct  1 17:18:05     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.au.AuResponse;

/**
 * A response to ask client to redraw the view of the specified
 * component or page.
 *
 * <p>data[0]: the uuid of the component or page to redraw
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class AuRedraw extends AuResponse {
	public AuRedraw(Component comp) {
		super("redraw", comp, new String[] {comp.getUuid()});
	}
	public AuRedraw(Page page) {
		super("redraw", page, new String[] {page.getUuid()});
	}
}
