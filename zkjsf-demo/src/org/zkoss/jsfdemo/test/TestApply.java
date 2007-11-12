/* TestApply.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 12, 2007 4:00:08 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsfdemo.test;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.ComposerExt;

/**
 * @author Dennis.Chen
 *
 */
public class TestApply implements Composer,ComposerExt{

	Component comp;
	
	
	public void doAfterCompose(Component comp) throws Exception {
		comp.setAttribute("doAfterCompose","doAfterCompose checked");
		this.comp = comp;
	}

	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.util.ComposerExt#doBeforeCompose(org.zkoss.zk.ui.Page, org.zkoss.zk.ui.Component, org.zkoss.zk.ui.metainfo.ComponentInfo)
	 */
	public ComponentInfo doBeforeCompose(Page page, Component parent, ComponentInfo compInfo) {
		comp.setAttribute("doBeforeCompose","doBeforeCompose checked");
		this.comp = comp;
		return compInfo;
	}

	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.util.ComposerExt#doBeforeComposeChildren(org.zkoss.zk.ui.Component)
	 */
	public void doBeforeComposeChildren(Component comp) throws Exception {
		comp.setAttribute("doBeforeComposeChildren","doBeforeComposeChildren checked");
		this.comp = comp;
	}

	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.util.ComposerExt#doCatch(java.lang.Throwable)
	 */
	public boolean doCatch(Throwable ex) throws Exception {
		comp.setAttribute("doCatch","doCatch checked");
		return false;
	}

	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.util.ComposerExt#doFinally()
	 */
	public void doFinally() throws Exception {
		comp.setAttribute("doFinally","doFinally checked");
	}

}
