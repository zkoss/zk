/* ScriptInitiator.java

{{IS_NOTE
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

import com.potix.zk.ui.Page;
import com.potix.zk.ui.metainfo.Script;
import com.potix.zk.ui.util.Initiator;

/**
 * An initiator used to evaluate a zscript file.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/05/29 04:28:07 $
 */
public class ScriptInitiator implements Initiator {
	private final Script _script;

	public ScriptInitiator(Script script) {
		if (script == null) throw new IllegalArgumentException("null");
		_script = script;
	}
	public void doInit(Page page, Object[] args) throws Exception {
		page.interpret(null, _script.getScript());
	}
	public void doCatch(Throwable ex) {
	}
	public void doFinally() {
	}
}
