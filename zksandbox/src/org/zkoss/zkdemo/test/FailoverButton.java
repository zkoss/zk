/* FailoverButton.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Apr 19 15:12:32     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;

/**
 * Used with test/failover.zul to test the failover mechanism.
 *
 * @author tomyeh
 * @see DumbFailoverManager
 */
public class FailoverButton extends Button {
	private boolean _recoverable;

	/** Returns whether it is recoverable.
	 */
	public boolean isRecoverable() {
		return _recoverable;
	}
	/** Sets whether it is recoverable.
	 */
	public void setRecoverable(boolean recoverable) {
		_recoverable = recoverable;
	}
	/** Handles the onClick event.
	 */
	public void onClick() throws InterruptedException {
		Object manager = ((WebAppCtrl)Executions.getCurrent()
			.getDesktop().getWebApp()).getFailoverManager();
		if (manager == null) {
			Messagebox.show("DumbFailoverManager is not configured. Specify it to zk.xml first.");
		} else if (!(manager instanceof DumbFailoverManager)) {
			Messagebox.show("DumbFailoverManager is required, not "+manager.getClass());
		} else {
			((DumbFailoverManager)manager).dropDesktop(_recoverable);
		}
	}
}
