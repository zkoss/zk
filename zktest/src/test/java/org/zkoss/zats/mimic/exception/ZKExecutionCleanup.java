/* ZKExecutionCleanup.java

	Purpose:

	Description:

	History:
		9:06 PM 2022/1/28, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zats.mimic.exception;

import java.util.List;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.ExecutionCleanup;

public class ZKExecutionCleanup implements ExecutionCleanup {

	public void cleanup(Execution exec, Execution parent, List errs)
			throws Exception {
		if (errs != null && errs.size() > 0) {
			String zats_id = exec.getHeader("ZATS_ID");
			ZKExceptionHandler.getInstance(zats_id).setExceptions(errs);
		}
	}


}
