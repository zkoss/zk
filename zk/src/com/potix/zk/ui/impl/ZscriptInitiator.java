/* ZscriptInitiator.java

{{IS_NOTE
	$Id: ZscriptInitiator.java,v 1.1 2006/05/25 04:10:56 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Thu May 25 10:50:36     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.impl;

import com.potix.util.resource.Locator;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.util.Initiator;

/**
 * An initiator used to evaluate a zscript file.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.1 $ $Date: 2006/05/25 04:10:56 $
 */
public class ZscriptInitiator implements Initiator {
	public ZscriptInitiator(Locator locator, String zsfile) {
	}
	public void doInit(Page page, Object[] args) {
	}
	public void doCatch(Throwable ex) {
	}
	public void doFinally() {
	}
}
