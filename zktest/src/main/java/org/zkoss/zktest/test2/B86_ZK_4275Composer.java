/* B86_ZK_4275Composer.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 30 15:50:31 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.concurrent.CompletableFuture;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Include;

/**
 * @author rudyhuang
 */
public class B86_ZK_4275Composer extends SelectorComposer<Component> {
	@Wire
	private Include inc;

	@Wire
	private Button btn;

	private Desktop desktop;

	protected String src = "B86-ZK-4275-include.zul";
	protected int count;

	@Listen("onClick = #btn")
	public void doClick() {
		CompletableFuture.runAsync(() -> {
			try {
				Executions.activate(desktop);
				Events.postEvent("onDoLater", btn, "");
			} catch (DesktopUnavailableException | InterruptedException e) {
				e.printStackTrace();
			} finally {
				Executions.deactivate(desktop);
			}
		});
		Clients.log("postEvent");
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		desktop = Executions.getCurrent().getDesktop();
		desktop.enableServerPush(true);
		btn.addEventListener("onDoLater", event -> {
			inc.setSrc(src + "?test=a" + count++);
			Clients.log("set Src");
		});
	}
}
