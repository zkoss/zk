/* GlobalCommandVM.java

	Purpose:
		
	Description:
		
	History:
		Tue May 04 16:24:42 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.viewmodel.command;

import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.DefaultGlobalCommand;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author rudyhuang
 */
public class GlobalCommandVM {
	@GlobalCommand
	public void clean() {
		Clients.log("GlobalCommandVM clean!");
	}

	@DefaultGlobalCommand
	public void unknownGlobalCommand(@ContextParam(ContextType.COMMAND_NAME) String cmdName) {
		Clients.log(String.format("[GlobalCommandVM] GlobalCommand [%s] unknown!", cmdName));
	}
}
