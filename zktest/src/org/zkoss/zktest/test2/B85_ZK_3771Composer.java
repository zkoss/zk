/* B85_ZK_3771VM.java

        Purpose:
                
        Description:
                
        History:
                Tue Jan 30 12:06 PM:00 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;


import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.util.Callback;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class B85_ZK_3771Composer extends SelectorComposer {

	@Wire
	private Window w1;
	@Wire
	private Label l1;

	@Listen("onClick=#b1")
	public void doWindowDetach() {
		w1.addCallback(ComponentCtrl.AFTER_PAGE_DETACHED, new Callback() {
			public void call(Object data) {
				l1.setValue("detach success");
			}
		});
		w1.detach();
	}
}
