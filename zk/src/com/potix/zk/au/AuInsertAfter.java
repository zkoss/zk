/* AuInsertAfter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 11:32:06     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.au;

import com.potix.zk.ui.Component;

/**
 * A response to insert an unparsed HTML after the specified component
 * at the client.
 * <p>data[0]: the uuid of the component after which the HTML will insert<br>
 * data[1]: the unparsed HTML (aka., content)
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class AuInsertAfter extends AuResponse {
	public AuInsertAfter(Component comp, String content) {
		super("addAft", comp, new String[] {comp.getUuid(), content});
	}
}
