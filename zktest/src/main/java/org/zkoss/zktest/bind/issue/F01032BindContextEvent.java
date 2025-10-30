package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.Event;

public class F01032BindContextEvent {

	String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Command
	@NotifyChange("message")
	public void cmd(@BindingParam("e") Event e1, @ContextParam(ContextType.TRIGGER_EVENT) Event e2,
			@ContextParam(ContextType.COMMAND_NAME) String cmd) {
		message = "evt1:" + e1.getName() + ",evt2:" + e2.getName() + ", cmd:" + cmd;
	}
}
