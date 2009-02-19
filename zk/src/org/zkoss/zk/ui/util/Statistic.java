/* Statistic.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Mar 14 23:32:51     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import java.util.List;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Desktop;

/**
 * An implementation of {@link Monitor} to accumulate statistic data
 * in memory.
 *
 * <p>It has no effect until you specify it in WEB-INF/zk.xml.
 *
 * @author tomyeh
 */
public class Statistic implements Monitor {
	private final long _startTime;
	private int _nsess, _actsess, _ndt, _actdt, _nupd, _actupd;

	public Statistic() {
		_startTime = System.currentTimeMillis();
	}

	/** Returns when the server (actually, this monitor) started.
	 */
	public long getStartTime() {
		return _startTime;
	}

	/** Returns the total number of sessions that have been created
	 * since the server started.
	 */
	public int getTotalSessionCount() {
		return _nsess;
	}
	/** Returns the number of active sessions.
	 */
	public int getActiveSessionCount() {
		return _actsess;
	}
	/** Returns the average number of sessions being created in an hour.
	 */
	public double getAverageSessionCount() {
		return _nsess / getEscapedHours();
	}

	/** Returns the total number of desktops that have been created
	 * since the server started.
	 */
	public int getTotalDesktopCount() {
		return _ndt;
	}
	/** Returns the number of active desktops.
	 */
	public int getActiveDesktopCount() {
		return _actdt;
	}
	/** Returns the average number of desktops being created in an hour.
	 */
	public double getAverageDesktopCount() {
		return _ndt / getEscapedHours();
	}

	/** Returns the total number of asynchronous updates that have been received
	 * since the server started.
	 */
	public int getTotalUpdateCount() {
		return _nupd;
	}
	/** Returns the number of active asynchronous updates.
	 */
	public int getActiveUpdateCount() {
		return _actupd;
	}
	/** Returns the average number of asynchronous updates being created
	 * in an hour.
	 */
	public double getAverageUpdateCount() {
		return _nupd / getEscapedHours();
	}

	/** Returns how many hours escaped since the server started.
	 */
	private double getEscapedHours() {
		long v = System.currentTimeMillis() - _startTime;
		return ((double)v) / 3600000;
	}

	//-- Monitor --//
	synchronized public void sessionCreated(Session sess) {
		++_nsess; ++_actsess;
	}
	synchronized public void sessionDestroyed(Session sess) {
		--_actsess;
	}
	synchronized public void desktopCreated(Desktop desktop) {
		++_ndt; ++_actdt;
	}
	synchronized public void desktopDestroyed(Desktop desktop) {
		--_actdt;
	}
	synchronized public void beforeUpdate(Desktop desktop, List requests) {
		++_nupd; ++_actupd;
	}
	synchronized public void afterUpdate(Desktop desktop) {
		--_actupd;
	}
}
