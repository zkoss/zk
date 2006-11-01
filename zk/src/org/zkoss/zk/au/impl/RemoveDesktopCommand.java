/* RemoveDesktopCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Mar 15 11:02:26     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.impl;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;

/**
 * A command to remove the specified desktop.
 * 
 * @author tomyeh
 */
public class RemoveDesktopCommand extends Command {
	public RemoveDesktopCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}
	protected void process(AuRequest request) {
		final Desktop dt = request.getDesktop();
		final WebAppCtrl wappc = (WebAppCtrl)dt.getWebApp();
		wappc.getDesktopCache(dt.getSession()).removeDesktop(dt);
	}
}
