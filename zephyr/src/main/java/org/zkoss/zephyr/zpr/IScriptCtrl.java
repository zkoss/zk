/* IScriptCtrl.java

	Purpose:

	Description:

	History:
		Fri Feb 25 15:26:34 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Script;

/**
 * An addition interface to {@link IScript}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IScriptCtrl {
	static IScript from(Script instance) {
		return new IScript.Builder()
				.from((IScript) instance)
				.build();
	}

	static String getEncodedSrcURL(String _src) {
		if (_src == null)
			return null;

		final Desktop dt = Executions.getCurrent().getDesktop(); //it might not belong to any desktop
		return dt != null ? dt.getExecution().encodeURL(_src) : null;
	}
}