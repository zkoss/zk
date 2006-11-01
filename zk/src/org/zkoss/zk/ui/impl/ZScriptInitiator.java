/* ZScriptInitiator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu May 25 10:50:36     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ZScript;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zk.ui.util.Namespace;
import org.zkoss.zk.ui.util.Namespaces;

/**
 * An initiator used to evaluate a zscript file.
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
		final Namespace ns = Namespaces.beforeInterpret(null, page);
		try {
			page.interpret(_zscript.getContent(page, null), ns);
		} finally {
			Namespaces.afterInterpret(ns);
		}
	}
	public void doCatch(Throwable ex) {
	}
	public void doFinally() {
	}
}
