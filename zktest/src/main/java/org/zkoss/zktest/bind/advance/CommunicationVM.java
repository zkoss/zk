/* CommunicationVM.java

		Purpose:
		
		Description:
		
		History:
				Fri May 07 11:35:24 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;

public class CommunicationVM {

	private String item = "init";

	public String getItem() {
		return item;
	}

	@GlobalCommand
	@NotifyChange("item")
	public void cmd1(@BindingParam("event") Event event) {
		item = event.getName() + ": cmd1";
		Clients.log("received the global command in VM");
	}
}
