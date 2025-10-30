package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * @author Vincent
 * 
 */
public class B65_ZK_2105 extends SelectorComposer<Window> {

	private static final long serialVersionUID = -3344321079575322219L;
	
	@WireVariable
	private Desktop desktop;
	
	@Wire
	private Vbox inf;
	
	@Listen("onClick = #startLongOp")
	public void doClick() {
		if (EventQueues.exists("longop")) {
			return; // busy
		}
		
		((org.zkoss.zk.ui.sys.DesktopCtrl) desktop).enableServerPush(new org.zkoss.zk.ui.impl.PollingServerPush(2000, 5000, -1));
		final EventQueue<Event> eq = EventQueues.lookup("longop");

		final EventListener<Event> longOp = new EventListener<Event>() {
			public void onEvent(Event evt) {
				if ("doLongOp".equals(evt.getName())) {
					org.zkoss.lang.Threads.sleep(1000);
					eq.publish(new Event("endLongOp", null));
				}
			}
		};
		
		final EventListener<Event> callback = new EventListener<Event>() {
			public void onEvent(Event evt) throws InterruptedException {
				if ("endLongOp".equals(evt.getName())) {
					EventQueues.remove("longop");
					org.zkoss.lang.Threads.sleep(1000);
					Executions.deactivate(desktop);
				}
			}
		};
		
		eq.subscribe(longOp, callback);
		eq.publish(new Event("doLongOp"));
	}
}
