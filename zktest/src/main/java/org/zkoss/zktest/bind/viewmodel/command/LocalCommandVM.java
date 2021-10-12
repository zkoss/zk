/* LocalCommandDuplicatedVM.java

	Purpose:
		
	Description:
		
	History:
		Tue May 04 15:43:31 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.viewmodel.command;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.DefaultCommand;
import org.zkoss.bind.annotation.DefaultGlobalCommand;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author rudyhuang
 */
public class LocalCommandVM {
	@Command
	@GlobalCommand("clean")
	public void command1() {
		Clients.log("command1");
	}

	@Command("command2")
	public void commandTwo() {
		Clients.log("commandTwo");
	}

	@Command({"command3", "command4"})
	public void command3And4() {
		Clients.log("command3And4");
	}

	@Command
	public void command5(@ContextParam(ContextType.TRIGGER_EVENT) MouseEvent event) {
		Clients.log("command5: " + event);
	}

	@DefaultCommand
	public void unknownCommand(@ContextParam(ContextType.COMMAND_NAME) String cmdName) {
		Clients.log(String.format("Command [%s] unknown!", cmdName));
	}

	@DefaultGlobalCommand
	public void unknownGlobalCommand(@ContextParam(ContextType.COMMAND_NAME) String cmdName) {
		Clients.log(String.format("[LocalCommandVM] GlobalCommand [%s] unknown!", cmdName));
	}
}
