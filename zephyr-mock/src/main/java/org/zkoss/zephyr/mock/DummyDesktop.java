/* DummyDesktop.java

	Purpose:

	Description:

	History:
		10:14 AM 2021/10/7, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.mock;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.impl.DesktopImpl;

/**
 * Mock {@link Desktop} implementation
 * @author jumperchen
 */
public class DummyDesktop extends DesktopImpl {
	public DummyDesktop(WebApp webApp) {
		super(webApp, "", "", "ajax", null);
	}

	public Execution getExecution() {
		return Executions.getCurrent();
	}
}
