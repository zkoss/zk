/* GlobalCommandParamsVM2.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 11:43:45 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.util.Clients;

public class GlobalCommandParamsVM2 {
	@Command
	public void local2(@BindingParam("name") String name) {
		Clients.log("Local Command local2 executed: " + name);
	}
}
