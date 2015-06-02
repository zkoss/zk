/**
 * 
 */
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

/**
 * @author jumperchen
 *
 */
public class B70_ZK_2747AsyncController extends SelectorComposer<Window> implements EventListener<Event> {
	private Desktop desktop;

	/**
	 * Creates a new thread and show a busy marker.
	 */
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		this.desktop = Executions.getCurrent().getDesktop();
		Executions.getCurrent().getDesktop().enableServerPush(true);

		final Thread t = new Thread(new Runner());
		t.start();
		Clients.showBusy("I am busy");
	}

	/**
	 * Second thread, do nothing only wait 100ms and report the a result.
	 *
	 */
	private class Runner implements Runnable {
		public void run() {
			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e) {
			}
			reportResult();
		}
	}

	/**
	 * Report a result called by creating an event by worker-thread.
	 */
	private void reportResult() {

		System.out.println("schedule... ");
		Executions.schedule(desktop, this, new Event("ready"));
//		try {
//			Executions.activate(desktop);
//			Clients.clearBusy();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			Executions.deactivate(desktop);
//		}
		
	}

	/**
	 * Clear the busy marker. Executed on gui-thread. Scheduled by
	 * {@link #reportResult()}.
	 */
	public void onEvent(Event ev) throws Exception {
			Clients.clearBusy();
          Clients.showNotification("test");
	}

}