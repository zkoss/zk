/* LocalCommandDuplicatedVM.java

	Purpose:
		
	Description:
		
	History:
		Tue May 04 15:43:31 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.viewmodel.command;

import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author rudyhuang
 */
public class LocalCommandDuplicatedVM {
	@Command
	public void command1() {
		Clients.log("command1");
	}

	@Command("command1")
	public void commandOne() {
		Clients.log("commandOne");
	}
}
