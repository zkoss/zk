/* ChildInitSuperNotExistVM.java

	Purpose:
		
	Description:
		
	History:
		Mon May 03 11:20:31 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.viewmodel.init;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Destroy;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author rudyhuang
 */
public class ChildInitSuperNotExistVM {
	protected String msg = "";

	@Init(superclass = true)
	public void childInit() {
		msg += "ChildInitSuperNotExistVM.childInit was called\n";
	}

	@Destroy
	public void childDestroy() {
		System.out.println("ChildInitSuperNotExistVM.childDestroy was called");
	}

	@Command
	public void printMessage() {
		Clients.log(msg);
	}
}
