/**
 * 
 */
package org.zkoss.zktest.test2;

import java.util.UUID;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 * 
 */
public class B70_ZK_2574 extends SelectorComposer<Div> {
	@Override
	public void doAfterCompose(Div comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
	}
	
	@Wire
	Label status;
	// based on
	// http://books.zkoss.org/wiki/ZK_Developer%27s_Reference/UI_Patterns/Long_Operations/Use_Event_Queues#A_Generic_Approach
	// --> fails
	@Listen("onClick=#btn")
	public void longOp() {
		final String tempWorkingQueueName = "workingQueue" + UUID.randomUUID();
		final EventQueue eventQueue = EventQueues.lookup(tempWorkingQueueName);

		eventQueue.subscribe(new EventListener() {
			public void onEvent(org.zkoss.zk.ui.event.Event event)
					throws Exception {
				if ("onStart".equals(event.getName())) {
					org.zkoss.lang.Threads.sleep(2000);
//					eventQueue.unsubscribe(this);
					eventQueue.publish(new Event("onFinish"));
				}
			}
		}, true);

		eventQueue.subscribe(new EventListener() {
			public void onEvent(org.zkoss.zk.ui.event.Event event)
					throws Exception {
				if ("onFinish".equals(event.getName())) {
					status.setValue("finished");
					Clients.clearBusy();
					EventQueues.remove(tempWorkingQueueName);
				}
			}
		});

		status.setValue("started");
		Clients.showBusy("please wait for 2 seconds");
		eventQueue.publish(new Event("onStart"));
	}

	// based on
	// http://books.zkoss.org/wiki/ZK_Developer%27s_Reference/UI_Patterns/Long_Operations/Use_Event_Queues#A_Simpler_Approach
	// --> works
	@Listen("onClick=#btn2")
	public void longOp2() {
		final String tempWorkingQueueName = "workingQueue" + UUID.randomUUID();
		final EventQueue eventQueue = EventQueues.lookup(tempWorkingQueueName);

		eventQueue.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				org.zkoss.lang.Threads.sleep(2000);
			}
		}, new EventListener() {
			public void onEvent(Event event) throws Exception {
				status.setValue("finished");
				Clients.clearBusy();
				EventQueues.remove(tempWorkingQueueName);
			}
		});

		status.setValue("started");
		Clients.showBusy("please wait for 2 seconds");
		eventQueue.publish(new Event("onStart"));
	}
}
