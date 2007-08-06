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
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.au.AuScript;

/**
 * A server-push implementation that is based on client-polling.
 *
 * @author tomyeh
 */
public class PollingServerPush implements ServerPush {
	private Desktop _desktop;

	/** Returns the JavaScript codes to enable (aka., start) the server push.
	 */
	protected String getStartScript() {
		final String start = _desktop.getWebApp()
			.getConfiguration().getPreference("PollingServerPush.start", null);
		return start != null ? start:
			"zk.load('zul.cpsp',function(){zkCpsp.start('"
				+ _desktop.getId() + "');});";
	}
	/** Returns the JavaScript codes to disable (aka., stop) the server push.
	 */
	protected String getStopScript() {
		final String stop = _desktop.getWebApp()
			.getConfiguration().getPreference("PollingServerPush.stop", null);
		return stop != null ? stop:
			"zkCpsp.stop('" + _desktop.getId() + "');";
	}


	//ServerPush//
	public void start(Desktop desktop) {
		_desktop = desktop;
		Clients.response(new AuScript(null, getStartScript()));
	}
	public void stop() {
		Clients.response(new AuScript(null, getStopScript()));
		_desktop = null;
	}

	public void onPiggyback() {
	}

	public void onActivate() {
	}
	public void onDeactivate() {
	}
}
