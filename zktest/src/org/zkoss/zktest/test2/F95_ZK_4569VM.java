/* F95_ZK_4569VM.java

	Purpose:
		
	Description:
		
	History:
		Fri May 8 11:32:33 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jameschu
 */
public class F95_ZK_4569VM {
	private String label = "a";

	@Command
	public void doClick() {
		Clients.log("Clicked");
	}

	@Command
	public void doClickA(@BindingParam("text") String text) {
		Clients.log("Clicked" + text);
	}

	@GlobalCommand
	public void doClickG() {
		Clients.log("ClickedG");
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
