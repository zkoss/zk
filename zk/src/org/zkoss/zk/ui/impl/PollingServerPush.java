/* PollingServerPush.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug  3 18:53:21     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.sys.ServerPush;

/**
 * A server-push implementation that is based on client-polling.
 *
 * @author tomyeh
 */
public class PollingServerPush implements ServerPush {
	private Desktop _desktop;
	/** The JavaScript codes to initialize the client. */
	private String _jsinit;

	public void start(Desktop desktop) {
		_desktop = desktop;
	}
	public void stop() {
	}

	public void onPiggyback() {
	}

	public void onActivate() {
	}
	public void onDeactivate() {
	}
}
