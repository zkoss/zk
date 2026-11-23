/* BasicPropertyBindingVM.java
	Purpose:

	Description:

	History:
		Thu May 06 16:43:47 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.databinding.commandbinding;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DefaultCommand;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jameschu
 */
public class CommandVM {
	private String selection = "123";
	@Command
	public void newOrder() {
		Clients.log("newOrder");
	}

	@Command("save")
	public void saveOrder() {
		Clients.log("saveOrder");
	}

	@DefaultCommand
	public void defaultAction() {
		Clients.log("defaultAction");
	}

	public String getSelection() {
		return selection;
	}
}
