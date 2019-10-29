/* B90_ZK_4340Composer.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 29 15:07:43 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.util.Composer;

/**
 * @author rudyhuang
 */
public class B90_ZK_4340Composer implements Composer {
	private Desktop desktop;
	private Component comp;
	private WebApp webapp;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		this.desktop = comp.getDesktop();
		this.comp = comp;
		this.webapp = WebApps.getCurrent();
		desktop.enableServerPush(true);
		CompletableFuture.supplyAsync(this::createComponents)
				.thenAccept(this::appendComponents)
				.whenComplete((t, ex) -> {
					if (ex != null) {
						ex.printStackTrace();
					}
				});
	}

	private Component[] createComponents() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted");
		}

		return Executions.createComponentsDirectly(webapp, "<textbox onChange=\"Clients.log(event.toString())\"/>", "zul", null);
	}

	private void appendComponents(Component[] components) {
		try {
			Executions.activate(desktop);
			Arrays.stream(components).forEach(comp::appendChild);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			Executions.deactivate(desktop);
		}
	}
}
