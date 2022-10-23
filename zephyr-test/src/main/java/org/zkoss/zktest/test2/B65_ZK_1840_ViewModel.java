package org.zkoss.zktest.test2;

import org.apache.commons.beanutils.BeanUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.Clients;

public class B65_ZK_1840_ViewModel {
	private String sequenceStatus;
	private String backgroundStatus;
	private String manualStatus;
	
	@Command("startSequencedOperation")
	public void onStartSequencedOperation() {
		launchSequencedOperation();
	}

	@Command("startBackgroundOperations")
	public void onStartBackgroundOperations() {
		launchBackgroundOperations();
	}
	
	@Command("startManualOperation")
	public void onStartManualOperation() {
		launchManualOperation();
	}
	
	@Command("startAllOperations")
	public void onStartLongOperation() {
		launchAllOperations();
	}

	private void launchAllOperations() {
		
		launchSequencedOperation();

		launchBackgroundOperations();

		launchManualOperation();
	}

	private void launchBackgroundOperations() {
		backgroundStatus = "";
		appendStatus("backgroundStatus", "BackgroundOperation ---> ");
		LongOperation backgroundOperation1 = backgroundOperation("backgroundQueue1", "backgroundStatus", 3000);
		LongOperation backgroundOperation2 = backgroundOperation("backgroundQueue2", "backgroundStatus", 5000);
		backgroundOperation1.start();
		backgroundOperation2.start();
	}

	private void launchSequencedOperation() {
		sequenceStatus = "";
		appendStatus("sequenceStatus", "SequencedOperation ---> ");
		LongOperation sequenceOperation = sequenceOperationChain();
		sequenceOperation.start();
	}

	private void launchManualOperation() {
		manualStatus = "";
		appendStatus("manualStatus", "ManualOperation ---> ");
		final Desktop desktop = Executions.getCurrent().getDesktop();
		desktop.enableServerPush(true);
		Thread manualOperation = manualOperation(desktop);
		manualOperation.start();
	}

	private LongOperation sequenceOperationChain() {
		//1. Operation
		BusyLongOperation firstOperation = busyOperation("loadQueue", "loading from DB", "sequenceStatus", 2000);
		//2. Operation
		BusyLongOperation secondOperation = busyOperation("processQueue", "processing in backend", "sequenceStatus", 2000);
		
		firstOperation.setNextOperation(secondOperation);
		return firstOperation;
	}

	private BusyLongOperation busyOperation(final String queueName, String busyMessage,
			final String statusField, final int duration) {
		return new BusyLongOperation(queueName, busyMessage) {
			protected void onStart() {
				super.onStart();
				appendStatus(statusField, queueName + " started ... ");
			}
			protected void execute() {
				sleep(duration);
			}
			
			protected void onFinish() {
				appendStatus(statusField, "done. ");
				super.onFinish();
			};
		};
	}
	
	private LongOperation backgroundOperation(final String queueName, final String statusField, final int duration) {
		return new LongOperation(queueName) {
			protected void onStart() {
				appendStatus(statusField, queueName + " started ... ");
			}
			protected void execute() {
				sleep(duration);
			}
			protected void onFinish() {
				appendStatus(statusField, queueName + " finished. ");
			}
		};
	}

	private Thread manualOperation(final Desktop desktop) {
		return new Thread() {
			public void run() {
				try {
					Executions.activate(desktop);
					appendStatus("manualStatus", "manual operation started ... ");
					Executions.deactivate(desktop);
					
					Thread.sleep(1000);
					Executions.activate(desktop);
					appendStatus("manualStatus", " 1 ");
					Executions.deactivate(desktop);
					Thread.sleep(1000);
					Executions.activate(desktop);
					appendStatus("manualStatus", " 2 ");
					Executions.deactivate(desktop);
					Thread.sleep(1000);
					Executions.activate(desktop);
					appendStatus("manualStatus", " 3 ");
					Executions.deactivate(desktop);
					Thread.sleep(1000);
					
					Executions.activate(desktop);
					appendStatus("manualStatus", "done");
					Executions.deactivate(desktop);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				desktop.enableServerPush(false);
			}
		};
	}

	private void appendStatus(String statusField, String newStatus) {
		System.out.println(statusField + newStatus);
		try {
			String currentStatus = BeanUtils.getProperty(this, statusField);
			BeanUtils.setProperty(this, statusField, currentStatus + newStatus);
		} catch (Exception e) {
			e.printStackTrace();
		}
		BindUtils.postNotifyChange(null, null, this, statusField);
	}
	
	public String getSequenceStatus() {
		return sequenceStatus;
	}

	public void setSequenceStatus(String sequenceStatus) {
		this.sequenceStatus = sequenceStatus;
	}

	public String getBackgroundStatus() {
		return backgroundStatus;
	}

	public void setBackgroundStatus(String backgroundStatus) {
		this.backgroundStatus = backgroundStatus;
	}

	public String getManualStatus() {
		return manualStatus;
	}

	public void setManualStatus(String manualStatus) {
		this.manualStatus = manualStatus;
	}

	

	// helper classes
	public static abstract class BusyLongOperation extends LongOperation {
		private String busyMessage;
		private LongOperation nextOperation;
		
		public void setNextOperation(LongOperation nextOperation) {
			this.nextOperation = nextOperation;
		}

		public BusyLongOperation(String queueName, String busyMessage) {
			super(queueName);
			this.busyMessage = busyMessage;
		}
		protected void onStart() {
			Clients.showBusy(busyMessage);
		}
		protected void onFinish() {
			Clients.clearBusy();
			if(nextOperation != null) {
				nextOperation.start();
			}
		}
	}
	
	public static abstract class LongOperation {
		private String queueName;
		
		public LongOperation(String queueName) {
			super();
			this.queueName = queueName;
		}
		
		abstract protected void onStart();
		abstract protected void execute();
		abstract protected void onFinish();
		
		public void start() {
			if (EventQueues.exists(queueName)) {
				Clients.showNotification("Queue with name '" + queueName + "' is already running. Try later.");
				return;
			}
			
			final EventQueue<Event> eventQueue = EventQueues.lookup(queueName, true);
			
			final EventListener<Event> executionListener = new EventListener<Event>() {
				public void onEvent(Event event) throws Exception {
					System.out.println("Execution Started " + queueName);
					execute();
					System.out.println("Execution Finished " + queueName);
				}
				
			};
			
			final EventListener<Event> callbackListener = new EventListener<Event>() {
				public void onEvent(Event event) throws Exception {
					eventQueue.unsubscribe(executionListener); //problem here
					EventQueues.remove(queueName); //or problem here
					onFinish();
				}
			};
			
			onStart();
			eventQueue.subscribe(executionListener, callbackListener);
			eventQueue.publish(new Event("launch dummy"));
		}
	}

	private void sleep(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
