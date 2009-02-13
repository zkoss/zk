/* ZScriptInitiator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu May 25 10:50:36     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.HashMap;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ZScript;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.Namespaces;

/**
 * An initiator used to evaluate a zscript file.
 * Used internally by {@link org.zkoss.zk.ui.metainfo.Parser} to evaluate
 * the init directive with zscript:<br/>
 * <code>&lt;?init zscript="xxx"?&gt;</code>
 *
 * @author tomyeh
 */
public class ZScriptInitiator implements Initiator {
	private final ZScript _zscript;

	public ZScriptInitiator(ZScript script) {
		if (script == null) throw new IllegalArgumentException("null");
		_zscript = script;
	}
	public void doInit(Page page, Object[] args) throws Exception {
		final HashMap backup = new HashMap();
		final Namespace ns = Namespaces.beforeInterpret(backup, page, false);
		try {
			page.interpret(
				_zscript.getLanguage(), _zscript.getContent(page, null), ns);
		} finally {
			Namespaces.afterInterpret(backup, ns, false);
		}
	}
	public void doAfterCompose(Page page) throws Exception {
	}
	public boolean doCatch(Throwable ex) {
		return false; //re-throw
	}
	public void doFinally() {
	}
}
