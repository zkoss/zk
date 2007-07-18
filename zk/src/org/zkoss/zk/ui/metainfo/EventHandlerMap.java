/* EventHandlerMap.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Mar 28 16:28:40     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

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
 * <p>It is thread safe.
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
	 * @since 2.5.0
	 */
	public EventHandler get(Component comp, String evtnm) {
		if (_evthds != null) {
			List ehl;
			synchronized (_evthds) {
				ehl = (List)_evthds.get(evtnm);
			}

			if (ehl != null) {
				synchronized (ehl) {
					for (Iterator it = ehl.iterator(); it.hasNext();) {
						final EventHandler eh = (EventHandler)it.next();
						if (eh.isEffective(comp))
							return eh;
					}
				}
			}
		}
		return null;
	}
	/** Returns an array of all event handlers associated
	 * with the specified event name, or null if no handler is associated
	 * with.
	 *
	 * <p>Unlike {@link #get(Component,String)}, it returns all
	 * event handlers no matter whether they are effective.
	 *
	 * @since 2.5.0
	 */
	public EventHandler[] getAll(String evtnm) {
		if (_evthds != null) {
			List ehl;
			synchronized (_evthds) {
				ehl = (List)_evthds.get(evtnm);
			}

			if (ehl != null) {
				synchronized (ehl) {
					return (EventHandler[])
						ehl.toArray(new EventHandler[ehl.size()]);
				}
			}
		}
		return null;
	}
	/** Returns the first event handler of the specified event name,
	 * or null if not available.
	 *
	 * <p>Deprecated since 2.5.0. Use {@link #get(Component, String)}
	 or {@link #getAll} instead.
	 * In other words, it is the same as getAll(evtnm)[0].
	 *
	 * @deprecated
	 */
	public EventHandler get(String evtnm) {
		if (_evthds != null) {
			List ehl;
			synchronized (_evthds) {
				ehl = (List)_evthds.get(evtnm);
			}

			if (ehl != null) {
				synchronized (ehl) {
					return (EventHandler)ehl.get(0); //must not empty
				}
			}
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
	 * @since 2.5.0
	 */
	public void add(String evtnm, EventHandler evthd) {
		if (evtnm == null || evthd == null)
			throw new IllegalArgumentException("null");

		if (_evthds == null) {
			synchronized (this) {
				if (_evthds == null)
					_evthds = new HashMap(3);
			}
		}

		List ehl;
		synchronized (_evthds) {
			ehl = (List)_evthds.get(evtnm);
			if (ehl == null) {
				_evthds.put(evtnm, ehl = new LinkedList());
				ehl.add(evthd);
				return;
			}
		}
		synchronized (ehl) {
			for (Iterator it = ehl.iterator(); it.hasNext();) {
				final EventHandler eh = (EventHandler)it.next();
				if (Objects.equals(eh.getCondition(), evthd.getCondition()))
					it.remove(); //replicate
			}
			ehl.add(evthd);
		}
	}
	/** Adds all event handlers of the specified map to this map.
	 */
	public void addAll(EventHandlerMap src) {
		if (src == null || src.isEmpty())
			return;

		synchronized (src._evthds) {
			for (Iterator it = src._evthds.entrySet().iterator();
			it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final String evtnm = (String)me.getKey();
				final List srcl = (List)me.getValue();
				synchronized (srcl) {
					for (Iterator e = srcl.iterator(); e.hasNext();)
						add(evtnm, (EventHandler)e.next());
				}
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
		if (_evthds != null) {
			synchronized (_evthds) {
				return "[evthd:" + _evthds + ']';
			}
		}
		return "[evthd:]";
	}
}
