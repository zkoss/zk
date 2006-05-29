/* RemoveDesktopCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Mar 15 11:02:26     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.au.impl;

import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.sys.WebAppCtrl;
import com.potix.zk.au.AuRequest;

/**
 * A command to remove the specified desktop.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.5 $ $Date: 2006/05/29 04:27:58 $
 */
public class RemoveDesktopCommand extends AuRequest.Command {
	public RemoveDesktopCommand(String evtnm, boolean skipIfEverError) {
		super(evtnm, skipIfEverError);
	}
	protected void process(AuRequest request) {
		final Desktop dt = request.getDesktop();
		final WebAppCtrl wappc = (WebAppCtrl)dt.getWebApp();
		wappc.getDesktopCache(dt.getSession()).removeDesktop(dt);
	}
}
