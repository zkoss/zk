/* B101_ZK_5730Composer.java

	Purpose:

	Description:

	History:
		12:10 PM 2024/10/9, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.http.SimpleSession;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;

/**
 * @author jumperchen
 */
public class B101_ZK_5730Composer extends SelectorComposer {

	@Wire private Grid grid;
	private Div rootDiv;
	private ListModelList model = new ListModelList();

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		rootDiv = (Div) comp;
		grid.setModel(model);
	}

	@Listen(Events.ON_CLICK + "=#logout")
	public void logout() {
		((SimpleSession)Sessions.getCurrent()).invalidateNow();
	}

	@Listen(Events.ON_CLICK + "=#op2")
	public void longOp() {
		fakeOperation(3);
		grid.addSclass("success");
	}

	static public void fakeOperation(int seconds) {
		long endTime = System.currentTimeMillis() + seconds * 1000;
		while (System.currentTimeMillis() < endTime) {
			// Just a busy-wait. In real scenarios, this could be some meaningful computation.
		}
	}
}