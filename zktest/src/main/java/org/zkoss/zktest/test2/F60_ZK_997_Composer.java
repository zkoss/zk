/* B60ZK997Controller.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Apr 3, 2012 10:28:51 AM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zkmax.ui.select.annotation.Subscribe;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

/**
 *
 * @author simonpai
 */
public class F60_ZK_997_Composer extends SelectorComposer<Component> {
	
	private static final long serialVersionUID = 1L;
	
	@Wire("#screen")
	private Div _screen;
	
	@Subscribe("myQueue")
	public void methodD0() {
		show("Event received through desktop queue: myQueue");
	}
	
	@Subscribe("myQueue")
	public void methodD0(Event event) {
		show("Event received through desktop queue: myQueue, event name: " + event.getName());
	}
	
	@Subscribe(value = "myQueue", scope=EventQueues.GROUP)
	public void methodG0() {
		show("Event received through group queue: myQueue");
	}
	
	@Subscribe(value = "myQueue", scope=EventQueues.GROUP)
	public void methodG0(Event event) {
		show("Event received through group queue: myQueue, event name: " + event.getName());
	}
	
	@Subscribe(value = "myQueue", scope=EventQueues.SESSION)
	public void methodS0() {
		show("Event received through session queue: myQueue");
	}
	
	@Subscribe(value = "myQueue", scope=EventQueues.SESSION)
	public void methodS0(Event event) {
		show("Event received through session queue: myQueue, event name: " + event.getName());
	}
	
	@Subscribe(value = "myQueue", scope=EventQueues.APPLICATION)
	public void methodA0() {
		show("Event received through application queue: myQueue");
	}
	
	@Subscribe(value = "myQueue", scope=EventQueues.APPLICATION)
	public void methodA0(Event event) {
		show("Event received through application queue: myQueue, event name: " + event.getName());
	}
	
	private void show(String msg) {
		Div d = new Div();
		d.appendChild(new Label(msg));
		_screen.appendChild(d);
	}
	
	@Listen("onClick = #dBtn")
	public void publishD() {
		publish(EventQueues.DESKTOP);
	}
	
	@Listen("onClick = #gBtn")
	public void publishG() {
		publish(EventQueues.GROUP);
	}
	
	@Listen("onClick = #sBtn")
	public void publishS() {
		publish(EventQueues.SESSION);
	}
	
	@Listen("onClick = #aBtn")
	public void publishA() {
		publish(EventQueues.APPLICATION);
	}
	
	private void publish(String scope) {
		EventQueue<Event> eq = EventQueues.lookup("myQueue", scope, true);
		eq.publish(new Event("onMyEvent"));
	}
	
}
