package org.zkoss.zktest.test2;

import java.util.Collections;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.ServerPush;
import org.zkoss.zk.ui.util.Clients;

public class B65_ZK_1777_ViewModel {

	private static final String GLOB_STATUS = "globStatus";
	private static final String UPDATE_STATUS = "onUpdateStatus";
	private static final String OP2 = "op2";
	private static final String OP1 = "op1";
	private String status1 = "idle";
	private String status2 = "idle";

	@Command
	public void start1() {
		startOperation(OP1, false);
	}

	@Command
	public void start2() {
		startOperation(OP2, true);
	}
	
	private void startOperation(final String operation, final boolean evenLonger) {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		System.out.println("current desktop: '" + desktop.getId() + "' ServerPush enabled: " + desktop.isServerPushEnabled());

		if(!desktop.isServerPushEnabled()) {
			System.out.println("enable server push only once for desktop: " + desktop.getId());
			desktop.enableServerPush(true);
		}
		
		sendGlobalStatusEvent(desktop, operation, "waiting");
		Clients.showBusy("working");
		
		final EventListener<StatusEvent> eventListener = new EventListener<B65_ZK_1777_ViewModel.StatusEvent>() {
			public void onEvent(StatusEvent statusEvent) throws Exception {
				sendGlobalStatusEvent(statusEvent);
			}
		};
		
		new Thread() {
			public void run() {
				try {
					if(evenLonger) {
						Thread.sleep(1000);
						Executions.schedule(desktop, eventListener, new StatusEvent(UPDATE_STATUS, operation, "working 1"));
						Thread.sleep(1000);
						Executions.schedule(desktop, eventListener, new StatusEvent(UPDATE_STATUS, operation, "working 2"));
						Thread.sleep(1000);
						Executions.schedule(desktop, eventListener, new StatusEvent(UPDATE_STATUS, operation, "working 3"));
						Thread.sleep(1000);
						Executions.schedule(desktop, eventListener, new StatusEvent(UPDATE_STATUS, operation, "working 4"));
						Thread.sleep(1000);
						Executions.schedule(desktop, eventListener, new StatusEvent(UPDATE_STATUS, operation, "working 5"));
						Thread.sleep(1000);
						Executions.schedule(desktop, eventListener, new StatusEvent(UPDATE_STATUS, operation, "working 6"));
						Thread.sleep(1000);
						Executions.schedule(desktop, eventListener, new StatusEvent(UPDATE_STATUS, operation, "working 7"));
						Thread.sleep(1000);
						Executions.schedule(desktop, eventListener, new StatusEvent(UPDATE_STATUS, operation, "working 8 (finishing)"));
					}
					Thread.sleep(2000);
					Executions.schedule(desktop, eventListener, new StatusEvent(UPDATE_STATUS, operation, "finished"));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
	
	private void sendGlobalStatusEvent(Desktop desktop, String operation, String status) {
		StatusEvent statusEvent = new StatusEvent(UPDATE_STATUS, operation, status);
		sendGlobalStatusEvent(statusEvent);
	}

	private void sendGlobalStatusEvent(StatusEvent statusEvent) {
		BindUtils.postGlobalCommand(null, null, GLOB_STATUS, Collections.<String, Object>singletonMap("event", statusEvent));
	}


	@GlobalCommand(GLOB_STATUS) 
	public void onUpdateStatus(@BindingParam("event") StatusEvent event) {
		String operation = event.getOperation();
		String status = event.getStatus();
		
		if(OP1.equals(operation)) {
			status1 = status;
			BindUtils.postNotifyChange(null, null, this, "status1");
		}
		if(OP2.equals(operation)) {
			status2 = status;
			BindUtils.postNotifyChange(null, null, this, "status2");
		}
		if(status.startsWith("working")) {
			Clients.clearBusy();
			Clients.showBusy(status);
		}
		if(status.startsWith("finish")) {
			Clients.clearBusy();
		}
	}
	
	public String getStatus1() {
		return status1;
	}

	public String getStatus2() {
		return status2;
	}
	
	public static class StatusEvent extends Event {

		private String operation;
		private String status;

		public StatusEvent(String name, String operation, String status) {
			super("name");
			this.operation = operation;
			this.status = status;
			
		}

		public String getOperation() {
			return operation;
		}

		public String getStatus() {
			return status;
		}
	}
}
