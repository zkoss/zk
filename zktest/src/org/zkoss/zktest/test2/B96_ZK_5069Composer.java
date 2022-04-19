/* B96_ZK_5069.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 18 10:50:21 CST 2022, Created by jameschu

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;

/**
 * @author jameschu
 */
public class B96_ZK_5069Composer extends SelectorComposer<Component> {

	@Wire
	private Button startBtn, stopBtn;

	ExecutorService executor = Executors.newFixedThreadPool(5);

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		Executions.getCurrent().getDesktop().enableServerPush(true);

	}

	@Listen(Events.ON_CLICK + "=#startBtn")
	public void startSpam(Event event) {
		Events.echoEvent("onLater", event.getTarget(), null);
	}


	@Listen("onLater=#startBtn")
	public void delayedStart() {
		Runnable worker1 = new B96_ZK_5069Thread("test1", Executions.getCurrent().getDesktop(), 200);

		Thread thread = new Thread(new B96_ZK_5069Thread("test1", Executions.getCurrent().getDesktop(), 200));
		thread.start();

		executor.submit(worker1);
	}
}