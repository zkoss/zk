/* B96_ZK_4787Composer.java

	Purpose:
		
	Description:
		
	History:
		Mon May 17 11:13:47 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Locale;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkmax.ui.util.Toast;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;

/**
 * @author rudyhuang
 */
public class B96_ZK_4787Composer extends SelectorComposer<Component> {
	private Desktop desktop;

	@Wire
	private Combobox cb;
	@Wire
	private Combobox cbSP;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		desktop = comp.getDesktop();
		desktop.enableServerPush(true);
	}

	@Listen("onOpen=#cb")
	public void handleNormal(){
		cb.setModel(new ListModelList<>(Locale.getAvailableLocales()));
		Toast.show("did sync update to popup");
	}

	@Listen("onOpen=#cbSP")
	public void handleServerPush(){
		doAsync(() -> {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				Executions.activate(desktop);
				cbSP.setModel(new ListModelList<>(Locale.getAvailableLocales()));
				Toast.show("did async update to popup");
				Executions.deactivate(desktop);
			} catch (DesktopUnavailableException | InterruptedException e) {
				e.printStackTrace();
			}
		});
	}

	private void doAsync(Runnable task) {
		try {
			Thread thread = new Thread(task);
			thread.start();
		} catch (DesktopUnavailableException e) {
			e.printStackTrace();
		}
	}
}