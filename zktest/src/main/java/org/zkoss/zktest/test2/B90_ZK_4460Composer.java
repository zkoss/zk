/* B90_ZK_4460Composer.java

	Purpose:
		
	Description:
		
	History:
		Mon Feb 24 18:31:32 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class B90_ZK_4460Composer extends SelectorComposer<Component> {
	private ScheduledExecutorService service;

	@Wire
	private Label currentDesktop;
	@Wire
	private Label currentTime;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		final Desktop desktop = Executions.getCurrent().getDesktop();
		if (!desktop.isServerPushEnabled()) {
			desktop.enableServerPush(true);
			service = Executors.newSingleThreadScheduledExecutor();
			service.scheduleAtFixedRate(() -> {
				Executions.schedule(desktop,
						event -> currentTime.setValue(LocalTime.now().withNano(0).toString()),
						new Event("dummy"));
			}, 0, 1, TimeUnit.SECONDS);
		}
		currentDesktop.setValue(desktop.getId());
	}
}
