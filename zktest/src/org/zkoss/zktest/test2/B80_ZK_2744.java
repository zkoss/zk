package org.zkoss.zktest.test2;

import org.zkoss.lang.Threads;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;



public class B80_ZK_2744 {
	static EventQueue que = EventQueues.lookup("chat", EventQueues.APPLICATION, true);
	
	public static class ServerPushEventListener implements EventListener {
		private Component info;
		public ServerPushEventListener(Component info) {
			this.info = info;
		}
        public void onEvent(Event evt) {
        	((Label) info).setValue(Integer.parseInt(((Label) info).getValue()) + 1 + "");;
        }
    }
	
	public static void longOperation() {
		try {
			Thread.sleep(10000);
			Clients.showNotification("done long operation");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void subscribeQueue(Component info) {
		new WorkingThread(info, true).start();
		que.subscribe(new ServerPushEventListener(info));
	}
	
	public static void start(Component info)
			throws InterruptedException {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		if (desktop.isServerPushEnabled()) {
			Messagebox.show("Already started");
		} else {
			desktop.enableServerPush(true);
			new WorkingThread(info).start();
		}
	}

	private static class WorkingThread extends Thread {

		private final Desktop _desktop;

		private final Component _info;
		private boolean isque;

		private WorkingThread(Component info) {
			_desktop = info.getDesktop();
			_info = info;
			isque = false;
		}
		private WorkingThread(Component info, boolean isque) {
			_desktop = info.getDesktop();
			_info = info;
			this.isque = isque;
		}

		public void run() {
			try {
				while (true) {
					if (!isque) task1();
					else task2();
				}
			} catch (DesktopUnavailableException ex) {
				System.out.println("The server push thread interrupted");
			} catch (InterruptedException e) {
				System.out.println("The server push thread interrupted");
			}
		}
		
		private void task1() throws InterruptedException, DesktopUnavailableException {
			if (_info.getDesktop() == null
					|| !_desktop.isServerPushEnabled()) {
				_desktop.enableServerPush(false);
				return;
			}
			Executions.activate(_desktop);
			try {
				((Label) _info).setValue(Integer.parseInt(((Label) _info).getValue()) + 1 + "");;
			} finally {
				Executions.deactivate(_desktop);
			}
			Threads.sleep(5000);
		}
		
		@SuppressWarnings("unchecked")
		private void task2() throws InterruptedException, DesktopUnavailableException {
			que.publish(new Event("onChat", _info, _info));
			Threads.sleep(5000);
		}
	}
}
