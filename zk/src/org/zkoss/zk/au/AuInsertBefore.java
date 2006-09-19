/* AuInsertBefore.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 11:30:18     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au;

import org.zkoss.zk.ui.Component;

/**
 * A response to insert an unparsed HTML before the specified component
 * at the client.
 * <p>data[0]: the uuid of the component before which the HTML will insert<br>
 * data[1]: the unparsed HTML (aka., content)
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class AuInsertBefore extends AuResponse {
	public AuInsertBefore(Component comp, String content) {
		super("addBfr", comp, new String[] {comp.getUuid(), content});
	}
}
