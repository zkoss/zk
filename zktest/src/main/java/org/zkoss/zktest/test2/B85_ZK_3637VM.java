/** B85_ZK_3637VM.java.

 Purpose:

 Description:

 History:
 	Tue June 6 17:14:22 CST 2017, Created by jameschu

 Copyright (C) 2017 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.ToServerCommand;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jameschu
 *
 */
@ToServerCommand({"doEventClickedLocal", "doEventClickedGlobal"})
public class B85_ZK_3637VM {

	@Command
	public void doEventClickedLocal() {
		Clients.log("clicked_local");
	}

	@GlobalCommand
	public void doEventClickedGlobal() {
		Clients.log("clicked_global");
	}
}