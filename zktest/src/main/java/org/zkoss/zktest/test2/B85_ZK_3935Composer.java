/* B85_ZK_3935Composer.java

	Purpose:
		
	Description:
		
	History:
		Fri May 25 17:30:33 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.DesktopCleanup;
import org.zkoss.zul.Div;

/**
 * @author rudyhuang
 */
public class B85_ZK_3935Composer extends SelectorComposer<Div> {
	private static final String QUEUE_NAME = "B85_ZK_3935_app";

	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);
		EventQueue<Event> queue = EventQueues.lookup(QUEUE_NAME, EventQueues.APPLICATION, true);
		queue.subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				System.out.println("nop");
			}
		});

		comp.getDesktop().addListener(new DesktopCleanup() {
			@Override
			public void cleanup(Desktop desktop) {
				EventQueue<Event> queue = EventQueues.lookup(QUEUE_NAME, EventQueues.APPLICATION, false);
				queue.publish(new Event("nop"));
			}
		});
	}

	@Listen("onClick = button")
	public void send() {
		EventQueue<Event> queue = EventQueues.lookup(QUEUE_NAME, EventQueues.APPLICATION, false);
		queue.publish(new Event("nop"));
	}
}
