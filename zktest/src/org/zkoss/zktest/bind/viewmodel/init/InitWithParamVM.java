/* InitWithParamVM.java

	Purpose:
		
	Description:
		
	History:
		Mon May 03 14:46:57 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.viewmodel.init;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Default;
import org.zkoss.bind.annotation.ExecutionParam;
import org.zkoss.bind.annotation.HeaderParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author rudyhuang
 */
public class InitWithParamVM extends InitWithParamParentVM {
	private String _test;
	private String _test2;

	@Init(superclass = true)
	public void childInit(@ExecutionParam("test") String test,
	                      @HeaderParam("x-non-exist") @Default("test2") String test2) {
		_test = test;
		_test2 = test2;
	}

	@Command
	public void printMessage() {
		Clients.log(_test + " " + _test2);
	}
}
