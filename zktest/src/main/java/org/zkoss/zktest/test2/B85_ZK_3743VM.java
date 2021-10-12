package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.lang.Threads;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.ListModelList;

/**
 * @author bob peng
 */
public class B85_ZK_3743VM {
	private ListModelList<Integer> desktopList = new ListModelList<Integer>();
	private EventQueue<Event> applicationQueue;
	private int count = 0;

	@Init
	public void init(@ContextParam(ContextType.APPLICATION) WebApp webApp) {
		applicationQueue = EventQueues.lookup("applicationQueue", webApp, true);
		applicationQueue.subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				if ("onIncrement".equals(event.getName())) {
					count++;
				}
			}
		}, true);
	}

	@Command
	public void triggerOnIncrement(@ContextParam(ContextType.DESKTOP) final Desktop desktop) {
		new Thread() {
			@Override
			public void run() {
				Threads.sleep(100);
				try {
					Executions.activate(desktop);
					applicationQueue.publish(new Event("onIncrement"));
					desktopList.clear();
					desktopList.add(count);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					Executions.deactivate(desktop);
				}
			}
		}.start();
	}

	public ListModelList<Integer> getDesktopList() {
		return this.desktopList;
	}
}


