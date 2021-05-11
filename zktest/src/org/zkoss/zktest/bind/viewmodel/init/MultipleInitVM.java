/* MultipleInitVM.java

	Purpose:
		
	Description:
		
	History:
		Mon May 03 16:01:30 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.viewmodel.init;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author rudyhuang
 */
public class MultipleInitVM {
	protected String msg = "";

	@Init
	public void init1() {
		msg += "InitVM.init1 was called\n";
	}

	@Init
	public void init2() {
		msg += "InitVM.init2 was called\n";
	}

	@Command
	public void printMessage() {
		Clients.log(msg);
	}
}
