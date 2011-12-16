/* EventListenerInfo.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 15 19:29:49 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.ComponentSerializationListener;
import org.zkoss.zk.ui.util.ComponentActivationListener;

/**
 * Used by {@link AbstractComponent} to hold the information of the event listener.
 * @author tomyeh
 * @since 6.0.0
 */
/*package*/ class EventListenerInfo
implements ComponentSerializationListener, ComponentActivationListener,
java.io.Serializable {
	/*package*/ final int priority;
	/*package*/ final EventListener<? extends Event> listener;

	/*package*/ EventListenerInfo(int priority, EventListener<? extends Event> listener) {
		this.priority = priority;
		this.listener = listener;
	}

	//ComponentSerializationListener//
	public void willSerialize(Component comp) {
		((AbstractComponent)comp).willSerialize(this.listener);
	}
	public void didDeserialize(Component comp) {
		((AbstractComponent)comp).didDeserialize(this.listener);
	}
	//ComponentActivationListener//
	public void didActivate(Component comp) {
		((AbstractComponent)comp).didActivate(this.listener);
	}
	public void willPassivate(Component comp) {
		((AbstractComponent)comp).willPassivate(this.listener);
	}
	public String toString() {
		return "[" + this.priority + ": " + this.listener.toString() + "]";
	}
}
