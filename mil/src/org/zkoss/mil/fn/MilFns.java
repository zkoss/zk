/* MilFns.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 30, 2007 4:40:28 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.mil.fn;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.Configuration;

/**
 * Utility for use EL.
 * 
 * @author henrichen
 */
public class MilFns {

	/**
	 * Get ZK Desktop and Page attributes.
	 */
	public static final String desktopAttrs(String action) {
		if (action == null)
			throw new IllegalArgumentException("null");

		final Desktop desktop = Executions.getCurrent().getDesktop();
		final WebApp wapp = desktop.getWebApp();
		final Configuration config = wapp.getConfiguration();
		final StringBuffer sb = new StringBuffer(64);
		sb.append(" za=\"").append(action).append("\"")
		.append(" zp=\"").append(config.getProcessingPromptDelay()).append("\"")
		.append(" zt=\"").append(config.getTooltipDelay()).append("\"")
		.append(" zv=\"").append(wapp.getVersion()).append("\"");
		return sb.toString();
	}
}
