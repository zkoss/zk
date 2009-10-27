/* MainLayoutInit.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 27, 2008 5:55:09 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.userguide;


import org.zkoss.lang.Library;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Initiator;

/**
 * @author jumperchen
 *
 */
public class MainLayoutInit implements Initiator {

	public void doAfterCompose(Page page) throws Exception {		
	}

	public boolean doCatch(Throwable ex) throws Exception {
		return false;
	}

	public void doFinally() throws Exception {		
	}

	public void doInit(Page page, java.util.Map args) throws Exception {
		if (Library.getProperty("org.zkoss.zkdemo.theme.silvergray") != null) {
			Library.setProperty("org.zkoss.zkdemo.theme.cookie", Themes.getSkinCookie(Executions.getCurrent()));
		}
	}

}
