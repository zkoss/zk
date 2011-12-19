/* EventListenerInfo.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 15 19:29:49 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;

import org.zkoss.util.logging.Log;
import org.zkoss.io.Serializables;

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

	//Utilties//
	/**
	 * @param comp the component used to invoke ComponentSerializationListener.
	 * Ignored if null.
	 */
	/*package*/ static final void write(java.io.ObjectOutputStream s,
	AbstractComponent comp, Map<String, List<EventListenerInfo>> listeners)
	throws IOException {
		if (listeners != null) {
			final Log logio = Serializables.logio;
			final boolean debug = logio.debugable();
			for (Map.Entry<String, List<EventListenerInfo>> me: listeners.entrySet()) {
				boolean keyWritten = false;
				final List<EventListenerInfo> ls = me.getValue();
				for (EventListenerInfo li: ls) {
					if (comp != null)
						comp.willSerialize(li.listener);

					if ((li.listener instanceof java.io.Serializable)
					|| (li.listener instanceof java.io.Externalizable)) {
						if (!keyWritten) {
							keyWritten = true;
							s.writeObject(me.getKey());
						}
						try {
							s.writeObject(li);
						} catch (java.io.NotSerializableException ex) {
							logio.error("Unable to serialize item: "+li.listener);
							throw ex;
						}
					} else if (debug) {
						logio.debug("Skip not-serializable item: "+li.listener);
					}
				}

				if (keyWritten)
					s.writeObject(null); //end of list for a particular event
			}
		}
		s.writeObject(null); //end of event listeners
	}
	/**
	 * @param comp the component used to invoke ComponentSerializationListener.
	 * Ignored if null.
	 */
	/*package*/ static final Map<String, List<EventListenerInfo>> read(
	java.io.ObjectInputStream s, AbstractComponent comp)
	throws IOException, ClassNotFoundException {
		Map<String, List<EventListenerInfo>> listeners = null;
		for (;;) {
			final String evtnm = (String)s.readObject();
			if (evtnm == null) break; //no more

			if (listeners == null)
				listeners = new HashMap<String,List<EventListenerInfo>>(4);
			final List<EventListenerInfo> ls =
				Serializables.smartRead(s, (List<EventListenerInfo>)null);
				//OK to use Serializables.smartRead to read back

			if (ls != null) {
				if (comp != null)
					for (EventListenerInfo li: ls)
						comp.didDeserialize(li.listener);

				listeners.put(evtnm, ls);
			}
		}
		return listeners;
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
