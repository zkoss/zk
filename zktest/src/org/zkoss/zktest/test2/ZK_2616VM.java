/** ZK_2616VM.java.

	Purpose:
		
	Description:
		
	History:
		10:03:57 AM Jun 4, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;

/**
 * @author jumperchen
 *
 */
public class ZK_2616VM {
	
	@Command
	@NotifyChange({ "count" })
	public void cmd() throws InterruptedException {
		synchronized (this) {
			wait(2000L);
		}
		Executions.sendRedirect("");
	}

	@Command
	public void cmd2() {
	}
}
