/* ChildInitOverrideVM.java

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
public class ChildInitOverrideVM extends InitVM {
	@Init(superclass = true)
	public void init() {
		msg += "ChildInitOverrideVM.init was called twice\n";
	}

	@Destroy(superclass = true)
	public void cleanup() {
		System.out.println("ChildInitOverrideVM.cleanup was called twice");
	}
}
