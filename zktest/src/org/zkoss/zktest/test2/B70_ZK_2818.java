/** B70_ZK_2818.java.

 Purpose:

 Description:

 History:
 11:03:57 AM Jul 23, 2015, Created by jameschu

 Copyright (C) 2015 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

/**
 * @author jameschu
 *
 */
public class B70_ZK_2818 extends SelectorComposer<Window>implements EventListener<Event> {

	private Desktop desktop;

	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		this.desktop = Executions.getCurrent().getDesktop();
		Executions.getCurrent().getDesktop().enableServerPush(true);

		Clients.showBusy(getSelf(), "I am busy");
		final Thread t = new Thread(new Runner());
		t.start();
	}

	/**
	 * Second thread, do nothing only wait 100ms and report the a result.
	 *
	 * @author PPI AG Informationstechnologie
	 * @version $Revision$
	 */
	private class Runner implements Runnable {
		public void run() {
			try {
				Thread.sleep(1);
				reportResult();
			} catch (final InterruptedException e) {
			}
		}
	}

	/**
	 * Report a result called by creating an event by worker-thread.
	 *
	 * @throws InterruptedException
	 * @throws DesktopUnavailableException
	 */
	private void reportResult() throws DesktopUnavailableException, InterruptedException {
		Executions.schedule(desktop, this, new Event("ready1"));
	}

	/**
	 * Clear the busy marker. Executed on gui-thread. Scheduled by
	 * {@link #reportResult()}.
	 */
	public void onEvent(Event ev) throws Exception {
		System.out.println("clear busy 1");
		Clients.clearBusy(getSelf());
		Executions.createComponents("B70-ZK-2818-1.zul", getSelf(), null);
	}
}
