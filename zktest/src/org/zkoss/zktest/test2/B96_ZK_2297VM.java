package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;

public class B96_ZK_2297VM {

	@Command
	public void someCommand(@ContextParam(ContextType.TRIGGER_EVENT) Event e) {
		Clients.log("" + e);
	}

	@Command
	public void someCommand2(@BindingParam("event") Event e) {
		Clients.log(e.getName());
	}

	@Command
	public void postGlobalCommand() {
		BindUtils.postGlobalCommand(null, null, "someGlobalCommand", null);
		BindUtils.postGlobalCommand(null, null, "someGlobalCommand2", null);
	}

	@GlobalCommand
	public void someGlobalCommand(@ContextParam(ContextType.TRIGGER_EVENT) Event e) {
		Clients.log("" + e);
	}

	@GlobalCommand
	public void someGlobalCommand2(@BindingParam("event") Event e) {
		Clients.log(e.getName());
	}
}