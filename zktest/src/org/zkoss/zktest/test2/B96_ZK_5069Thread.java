/* B96_ZK_5069.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 18 10:50:21 CST 2022, Created by jameschu

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;

/**
 * @author jameschu
 */
public class B96_ZK_5069Thread implements Runnable {

	public static final String TEST_QUEUE = "TEST_QUEUE";
	private EventListener[] listeners;
	private String prefix;
	private int count;
	private Desktop desktop;

	public B96_ZK_5069Thread(String prefix, Desktop desktop, int count) {
		super();
		this.prefix = prefix;
		this.count = count;
		this.desktop = desktop;
		listeners = new EventListener[count];
	}

	@Override
	public void run() {
		System.out.println("start");
		try {
			Executions.activate(desktop);
			EventQueue<Event> queue = EventQueues.lookup(TEST_QUEUE, true);
			Executions.deactivate(desktop);
			for (int i = 0; i < count; i++) {
				EventListener<Event> listener = new EventListener<Event>() {
					@Override
					public void onEvent(Event event) throws Exception {
						System.out.println(prefix + "foo");
					}
				};
				listeners[i] = listener;
				queue.subscribe(listener);
				System.out.println("subscribed " + i);
				Thread.sleep(5);
			}
			for (int i = 0; i < count; i++) {
				queue.unsubscribe(listeners[i]);
				System.out.println("unsubscribed " + i);
				Thread.sleep(5);
			}

		} catch (DesktopUnavailableException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("end");
	}
}