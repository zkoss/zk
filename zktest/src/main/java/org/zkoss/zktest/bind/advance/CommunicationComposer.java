/* CommunicationComposer.java

		Purpose:
		
		Description:
		
		History:
				Fri May 07 11:30:51 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.GlobalCommandEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.ui.select.annotation.Subscribe;
import org.zkoss.zul.Label;

public class CommunicationComposer extends SelectorComposer {
	@Wire
	private Label l1;

	@Listen("onClick=#btn1")
	public void cmd1(Event fe) {
		Map<String,Object> args = new HashMap<>();
		args.put("event", fe);
		BindUtils.postGlobalCommand(null, null, "cmd1", args);
		Clients.log("Send a global command in a composer");
	}

	@Subscribe("myqueue")
	public void cmd2(Event evt) {
		if (evt instanceof GlobalCommandEvent) {
			if ("cmd2".equals(((GlobalCommandEvent)evt).getCommand())) {
				l1.setValue(evt.getName() + ": cmd2");
				Clients.log("received the global command in a composer");
			}
		}
	}
}
