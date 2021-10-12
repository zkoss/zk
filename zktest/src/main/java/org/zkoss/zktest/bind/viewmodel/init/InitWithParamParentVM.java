/* InitWithParamParentVM.java

	Purpose:
		
	Description:
		
	History:
		Mon May 03 14:37:20 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.viewmodel.init;

import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Execution;

/**
 * @author rudyhuang
 */
public abstract class InitWithParamParentVM {
	protected Execution _execution;

	@Init
	public void init(@ContextParam(ContextType.EXECUTION) Execution execution) {
		_execution = execution;
		_execution.setAttribute("test", "test");
	}
}
