/* VoidComposer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 26 11:02:46     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test2;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.ComposerExt;
import org.zkoss.zul.Window;
/**
 * A composer that denies the creation of a component.
 *
 * @author tomyeh
 */
public class VoidComposer implements Composer, ComposerExt {
	public void doAfterCompose(Component comp) {
	}
	public ComponentInfo doBeforeCompose(Page page, Component parent,
	ComponentInfo compInfo) {
		return null; //so ZK loader won't create it
	}
	public void doBeforeComposeChildren(Component comp) throws Exception {
	}
	public boolean doCatch(Throwable ex) throws Exception {
		return false;
	}
	public void doFinally() throws Exception {
	}
}
