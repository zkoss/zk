/* AuSetTitle.java

{{IS_NOTE
	$Id: AuSetTitle.java,v 1.2 2006/02/27 03:54:44 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 10:31:55     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.au;

/**
 * A response to ask the client to set the title (of window).
 *  <p>data[0]: the title
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:54:44 $
 */
public class AuSetTitle extends AuResponse {
	public AuSetTitle(String title) {
		super("title", title);
	}
}
