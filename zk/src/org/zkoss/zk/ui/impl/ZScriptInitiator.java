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

import java.util.Map;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ZScript;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.ext.Scopes;

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
	public void doInit(Page page, Map args) throws Exception {
		final Scope scope = Scopes.beforeInterpret(page);
		try {
			page.interpret(
				_zscript.getLanguage(), _zscript.getContent(page, null), scope);
		} finally {
			Scopes.afterInterpret();
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
