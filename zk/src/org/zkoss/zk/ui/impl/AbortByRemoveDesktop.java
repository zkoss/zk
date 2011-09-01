/* AbortByRemoveDesktop.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Nov  6 21:46:23     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.AbortingReason;
import org.zkoss.zk.au.AuResponse;

/**
 * The aborting reason when the remove-desktop command is received.
 *
 * @author tomyeh
 */
public class AbortByRemoveDesktop implements AbortingReason {
	private static final Log log = Log.lookup(AbortByRemoveDesktop.class);

	public AbortByRemoveDesktop() {
	}

	//-- AbortingReason --//
	public boolean isAborting() {
		return true;
	}
	public void execute() {
	}
	public AuResponse getResponse() {
		return null;
	}
	public void finish() {
		final Execution exec = Executions.getCurrent();

		//Bug 1753712: disable visualizer since responses were gen.
		((ExecutionCtrl)exec).getVisualizer().disable();

		//Bug 1868371: we shall postpone the cleanup to the last step
		DesktopRecycles.removeDesktop(exec);
	}
}
