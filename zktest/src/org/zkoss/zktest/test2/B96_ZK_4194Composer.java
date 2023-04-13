/* B96_ZK_4194Composer.java

	Purpose:
		
	Description:
		
	History:
		3:25 PM 2023/4/12, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.time.Instant;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.DesktopCleanup;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Separator;
/**
 * @author jumperchen
 */
public class B96_ZK_4194Composer implements Composer<Component> {

	public static final String GLOBAL_QUEUE = "global-queue";

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		final WebApp webapp = WebApps.getCurrent();
		final Session session = Sessions.getCurrent();

		final EventQueue<Event> eq = EventQueues.lookup(GLOBAL_QUEUE, WebApps.getCurrent(), true);

		final Div eventLog = (Div) comp.getFellow("eventLog");

		comp.insertBefore(scheduleButton(eq), eventLog);

		final EventListener<Event> eqListener = event -> {
			eventLog.appendChild(new Label(event.toString() + event.getData()));
			eventLog.appendChild(new Separator());
		};

		final Desktop desktop = comp.getDesktop();
		System.out.println("subscribe: " + desktop.getId());
		eq.subscribe(eqListener);


		desktop.addListener((DesktopCleanup) destroyedDesktop -> {
			System.out.println("unsubscribe: " + destroyedDesktop.getId());
			eq.unsubscribe(eqListener);
		});
	}

	private Button scheduleButton(EventQueue<Event> eq) {
		final Button scheduleButton = new Button("schedule event");
		scheduleButton.addEventListener(Events.ON_CLICK, e -> {
			//background thread triggering the event
			new Thread(() -> {
				try {
					Thread.sleep(500);
					eq.publish(new Event("onBackground", null, "event time: " + Instant.now()));
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}).start();
		});
		return scheduleButton;
	}
}