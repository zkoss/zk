/* B85_ZK_3692.java

	Purpose:
		
	Description:
		
	History:
		Mon Aug 14 12:04:03 CST 2017, Created by wenninghsu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Separator;

/**
 * 
 * @author wenninghsu
 */
public class B85_ZK_3692 extends SelectorComposer<Component> {
private static final long serialVersionUID = 1L;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		final Desktop desktop = Executions.getCurrent().getDesktop();
		desktop.enableServerPush(true);

		for (int i = 0; i < 2; i++) {
			final Label label = new Label("WAITING");
			label.setId("lb" + i);
			comp.appendChild(label);
			comp.appendChild(new Separator((((i + 1) % 20) == 0) ? "horizontal" : "vertical"));
			final int j = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(3000);
						Executions.activate(desktop);
						label.setValue("___:" + j);
					} catch (DesktopUnavailableException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						Executions.deactivate(desktop);
					}
				}
			}).start();

		}


	}

}
