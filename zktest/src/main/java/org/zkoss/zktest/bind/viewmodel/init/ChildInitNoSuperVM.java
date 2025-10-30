/* ChildInitNoSuperVM.java

	Purpose:
		
	Description:
		
	History:
		Mon May 03 11:20:31 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.viewmodel.init;

import org.zkoss.bind.annotation.Destroy;
import org.zkoss.bind.annotation.Init;

/**
 * @author rudyhuang
 */
public class ChildInitNoSuperVM extends InitVM {
	@Init
	public void childInit() {
		msg += "ChildInitNoSuperVM.childInit was called\n";
	}

	@Destroy
	public void childDestroy() {
		System.out.println("ChildInitNoSuperVM.childDestroy was called");
	}
}
