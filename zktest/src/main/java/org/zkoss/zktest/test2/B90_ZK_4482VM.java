/* B90_ZK_4482VM.java

		Purpose:
		
		Description:
		
		History:
				Mon Jan 13 18:12:42 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.NotifyCommand;
import org.zkoss.bind.annotation.ToClientCommand;
import org.zkoss.bind.annotation.ToServerCommand;
import org.zkoss.zk.ui.util.Clients;

@NotifyCommand(value = "update", onChange = "_vm_.data")
@ToClientCommand({"update"})
@ToServerCommand({"reload"})
public class B90_ZK_4482VM {
	private String data = "dataString";
	
	@Command
	@NotifyChange("data")
	public void reload() {
		Clients.log("vm reload");
	}
	
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}
}
