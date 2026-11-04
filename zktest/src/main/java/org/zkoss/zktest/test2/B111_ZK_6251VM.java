/* B111_ZK_6251VM.java

	Purpose:

	Description:

	History:
		Wed Nov 04 09:16:59 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;

public class B111_ZK_6251VM {
	private int count;

	@Command
	public void notifyNullBean(@ContextParam(ContextType.VIEW) Component view) {
		view.setAttribute("foo", "foo " + ++count);
		BindUtils.postNotifyChange(null, null, null, "*");
	}
}
