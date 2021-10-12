/* InitVM.java

	Purpose:
		
	Description:
		
	History:
		Mon May 03 10:55:07 CST 2021, Created by rudyhuang

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
public class InitVM {
	protected String msg = "";

	@Init
	public void init() {
		msg += "InitVM.init was called\n";
	}

	public void noinit() {
		msg += "InitVM.noinit was never called\n";
	}

	@Destroy
	public void cleanup() {
		System.out.println("InitVM.cleanup was called");
	}

	@Command
	public void printMessage() {
		Clients.log(msg);
	}
}
