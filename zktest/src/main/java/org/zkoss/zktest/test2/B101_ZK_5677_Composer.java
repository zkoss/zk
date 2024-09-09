/* B101_ZK_5677_Composer.java

	Purpose:

	Description:

	History:
		10:48 AM 2024/9/9, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;

/**
 * @author jumperchen
 */
public class B101_ZK_5677_Composer extends SelectorComposer<Component> {


	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		Executions.getCurrent().getDesktop().enableServerPush(true);
	}

	public static int errorCount = 0;
	@Listen("onClick=#btn")
	public void scheduleEvent() {
		Desktop dt = Executions.getCurrent().getDesktop();
		Runnable runnable = () -> {
			if (errorCount < 3) {
				throw new NullPointerException("test: "	+ ++errorCount);
			}
		};
		EventListener executeEventListener = (evt)->{
			Thread.sleep(500); //simulate processing time
			runnable.run();
		};
		try {
			Executions.schedule(dt, executeEventListener, new Event("foo"));
		} catch (Exception e) {
			String msg = "scheduling error";
			throw new RuntimeException(msg,e);
		}
	}

}