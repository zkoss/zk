package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.zk.ui.util.Clients;

public class B95_ZK_4723_1VM {
	@GlobalCommand
	public void loadValue(@BindingParam("parameter") String parameter, @BindingParam("anotherParameter") String anotherParameter) {
		Clients.log("VM2 called global command with value " + parameter + " and " + anotherParameter);
	}
}