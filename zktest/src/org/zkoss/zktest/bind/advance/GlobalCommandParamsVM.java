/* GlobalCommandParamsVM.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 11:24:25 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.zk.ui.util.Clients;

public class GlobalCommandParamsVM {
	@GlobalCommand
	public void global1(@BindingParam("name") String name) {
		Clients.log("GlobalCommand global1 executed: " + name);
	}

	@GlobalCommand
	public void global2(@BindingParam("name") String name) {
		Clients.log("GlobalCommand global2 executed: " + name);
	}
}
