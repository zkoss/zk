/* B101_ZK_5730_1Composer.java

	Purpose:

	Description:

	History:
		12:32 PM 2024/10/9, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Optional;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 */
public class B101_ZK_5730_1Composer implements Composer {
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		Desktop dt = Executions.getCurrent().getDesktop();
		dt.enableServerPush(true);

		Button button = new Button("schedule");
		Label myLabel = new Label("my label");

		comp.appendChild(button);
		comp.appendChild(myLabel);

		button.addEventListener(Events.ON_CLICK, event -> {
			Runnable runnable = () -> {
				System.out.println("runnable - run()");
				myLabel.addSclass("bg-red");
			};
			EventListener<? super Event> executeEventListener = this::executeEventListener;
			Executions.schedule(dt, executeEventListener, new Event("ExecuteEvent", null, runnable));

			// This causes an NPE in the runnable above
			((PageCtrl)comp.getPage()).destroy();
		});

	}

	public void executeEventListener(Event event) {
		Optional.ofNullable((Runnable)event.getData()).ifPresent(Runnable::run);
	}
}