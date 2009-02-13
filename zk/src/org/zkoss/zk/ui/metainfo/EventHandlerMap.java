/* EventHandlerMap.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Mar 28 16:28:40     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Set;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;

/**
 * A map of instances of {@link EventHandler}.
 *
 * <p>Note: it is not thread safe. Thus, it is better to {@link #clone}
 * and then modifying the cloned instance if you want to change it
 * concurrently.
 *
 * @author tomyeh
 */
public class EventHandlerMap implements Cloneable, java.io.Serializable {
	/** The event handler map, (String evtnm, EventHandler evthd).
	 */
	private Map _evthds;

	/** Returns whether no event handler at all.
	 */
	public boolean isEmpty() {
		return _evthds == null || _evthds.isEmpty();
	}
	/** Returns the first effective event handler of the specified event name,
	 * or null if not available.
	 *
	 * <p>It checks whether an event handler is effective by calling
	 * {@link EventHandler#isEffective(Component)}.
	 *
	 * @param comp the component used to evaluate whether an event handler
	 * is effective.
	 * @see EventHandler#isEffective(Component)
	 * @since 3.0.0
	 */
	public EventHandler get(Component comp, String evtnm) {
		if (_evthds != null) {
			final List ehl = (List)_evthds.get(evtnm);
			if (ehl != null) {
				for (Iterator it = ehl.iterator(); it.hasNext();) {
					final EventHandler eh = (EventHandler)it.next();
					if (eh.isEffective(comp))
						return eh;
				}
			}
		}
		return null;
	}
	/** Returns a readonly collection of event names (String), or
	 * an empty collection if no event name is registered.
	 * @since 3.0.2
	 */
	public Set getEventNames() {
		return _evthds != null ? _evthds.keySet(): Collections.EMPTY_SET;
	}
	/** Returns a readonly list of all event handlers associated
	 * with the specified event name, or null if no handler is associated
	 * with.
	 *
	 * <p>Unlike {@link #get(Component,String)}, it returns all
	 * event handlers no matter whether they are effective.
	 *
	 * @since 3.0.0
	 */
	public List getAll(String evtnm) {
		if (_evthds != null)
			return (List)_evthds.get(evtnm);
		return null;
	}
	/**
	 * @deprecated As of release 3.0.0, replaced by getAll(evtnm)[0].
	 * See also {@link #get(Component, String)} or {@link #getAll}
	 */
	public EventHandler get(String evtnm) {
		if (_evthds != null) {
			final List ehl = (List)_evthds.get(evtnm);
			if (ehl != null)
				return (EventHandler)ehl.get(0); //must not empty
		}
		return null;
	}

	/** Adds the event handler for the specified event name.
	 * <p>Note: the new handler won't overwrite the previous one,
	 * unless {@link EventHandler#getCondition} is the same.
	 * Rather, the new handler is appended to the list. You can retreive
	 * list by invoking {@link #getAll}.
	 *
	 * @see #getAll
	 * @since 3.0.0
	 */
	public void add(String evtnm, EventHandler evthd) {
		if (evtnm == null || evthd == null)
			throw new IllegalArgumentException("null");

		if (_evthds == null)
			_evthds = new HashMap(4);

		List ehl = (List)_evthds.get(evtnm);
		if (ehl == null) {
			_evthds.put(evtnm, ehl = new LinkedList());
		} else {
			for (Iterator it = ehl.iterator(); it.hasNext();) {
				final EventHandler eh = (EventHandler)it.next();
				if (Objects.equals(eh.getCondition(), evthd.getCondition()))
					it.remove(); //replicate
			}
		}

		ehl.add(evthd);
	}
	/** Adds all event handlers of the specified map to this map.
	 */
	public void addAll(EventHandlerMap src) {
		if (src != null && !src.isEmpty()) {
			for (Iterator it = src._evthds.entrySet().iterator();
			it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final String evtnm = (String)me.getKey();
				final List srcl = (List)me.getValue();
				for (Iterator e = srcl.iterator(); e.hasNext();)
					add(evtnm, (EventHandler)e.next());
			}
		}
	}

	//Cloneable//
	/** Clones this event handler map.
	 */
	public Object clone() {
		final EventHandlerMap clone = new EventHandlerMap();
		clone.addAll(this);
		return clone;
	}
	//Object//
	public String toString() {
		return "[evthd:" + _evthds + ']';
	}
}
