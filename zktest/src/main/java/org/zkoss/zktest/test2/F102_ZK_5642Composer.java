package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.Clients;

public class F102_ZK_5642Composer extends SelectorComposer<Component> {
	private EventQueue<Event> queue;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		Executions.getCurrent().getDesktop().enableServerPush(true);
		queue = EventQueues.lookup("test");

		queue.subscribe(e -> {
			Thread.sleep(2000);
			System.out.println("did async - " + e.getName());
		}, e -> {
			System.out.println("did sync - " + e.getName());
			Clients.log("server push done - " + e.getName());
		});
		queue.publish(new Event("foo"));
		Clients.log("composer doAfterCompose");
	}

	@Listen("onClick=#btn")
	public void onClick() {
		Clients.log("clicked");
		queue.publish(new Event("clicked"));
	}
}