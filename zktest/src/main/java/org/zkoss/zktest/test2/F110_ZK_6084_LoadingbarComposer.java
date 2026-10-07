/* F110_ZK_6084_LoadingbarComposer.java

		Purpose:

		Description:

		History:
				Mon Sep 07 17:20:00 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zkmax.ui.util.Loadingbar;
import org.zkoss.zkmax.ui.util.LoadingbarControl;

public class F110_ZK_6084_LoadingbarComposer extends SelectorComposer<Component> {
	private LoadingbarControl ctrl;

	@Listen("onClick = #start")
	public void start() {
		ctrl = Loadingbar.createLoadingbar();
		ctrl.start(0);
	}

	@Listen("onClick = #to26")
	public void to26() {
		ctrl.update(26);
	}

	@Listen("onClick = #to27")
	public void to27() {
		ctrl.update(27);
	}

	@Listen("onClick = #to51")
	public void to51() {
		ctrl.update(51);
	}

	@Listen("onClick = #indeterminate")
	public void indeterminate() {
		ctrl.update(true);
	}
}
