/* AuEndOverlapped.java

{{IS_NOTE
	$Id: AuEndOverlapped.java,v 1.2 2006/02/27 03:54:43 tomyeh Exp $
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

/**
 * A response to restore an overlapped window back to embeded at the client
 * <p>data[0]: the uuid of the component to become embeded
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:54:43 $
 */
public class AuEndOverlapped extends AuResponse {
	public AuEndOverlapped(Component comp) {
		super("endOvl", comp, comp.getUuid());
	}
}
