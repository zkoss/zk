/* EventListenerMapImpl.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 15 19:23:17 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.metainfo.EventHandlerMap;
import org.zkoss.zk.ui.sys.EventListenerMap;
import org.zkoss.zk.ui.sys.EventListenerMapCtrl;

/**
 * An implementation of {@link EventListenerMap} used by {@link AbstractComponent}.
 * @author tomyeh
 * @since 6.0.0
 */
/*package*/ class EventListenerMapImpl implements EventListenerMap,
		EventListenerMapCtrl, java.io.Serializable {
	/** A map of event listeners. */
	private transient Map<String, List<EventListenerInfo>> _listeners;
	/** A map of event handler to handle events. */
	private final EventHandlerMap _evthds;

	EventListenerMapImpl(Map<String, List<EventListenerInfo>> listeners, EventHandlerMap evthds) {
		_listeners = listeners;
		_evthds = evthds;
	}

	public void service(Event event, Scope scope, Component comp, String cmd) throws Exception {
		((AbstractComponent) comp).service(event, scope, _listeners != null ? _listeners.get(cmd) : null,
				_evthds != null ? _evthds.get(comp, cmd) : null, null, true); //skip page's listener
	}

	//Serializable//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();

		EventListenerInfo.write(s, null, _listeners); //no AbstractComponent
	}

	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		_listeners = EventListenerInfo.read(s, null); //no AbstractComponent
	}

	public Set<String> getEventNames() {
		Set<String> listeners = _listeners != null ? _listeners.keySet() : Collections.emptySet();
		Set<String> events = _evthds != null ? _evthds.getEventNames() : Collections.emptySet();
		return Stream.concat(listeners.stream(), events.stream())
				.collect(Collectors.toSet());
	}
}
