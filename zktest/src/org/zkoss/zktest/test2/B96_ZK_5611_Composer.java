/* B96_ZK_5611_Composer.java

        Purpose:

        Description:

        History:
                Thu Jan 25 17:11:30 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.concurrent.CompletableFuture;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;

public class B96_ZK_5611_Composer extends SelectorComposer {

	private int hlayoutId;
	private Component comp;
	private Desktop desktop;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		this.comp = comp;
		Executions.getCurrent().getDesktop().enableServerPush(true);
		hlayoutId = 0;
		desktop = comp.getDesktop();
	}

	@Listen("onClick = button")
	public void start(){
		Hlayout hlayout = new Hlayout();
		hlayout.setId("hlayout" + hlayoutId++);
		comp.appendChild(hlayout);
		for (int i = 0 ; i < 3 ; i++) {
			String taskId = String.valueOf(i);
			CompletableFuture.runAsync(() -> {
				Executions.schedule(desktop,  event -> {
					hlayout.appendChild(new Label(taskId));
				}, new Event("myEvent"));
			});
		}
	}
}