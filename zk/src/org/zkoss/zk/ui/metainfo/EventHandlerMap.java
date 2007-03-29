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

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collections;

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
	/** Returns the event handler of the specified event name,
	 * or null if not available.
	 */
	public EventHandler get(String evtnm) {
		return _evthds != null ? (EventHandler)_evthds.get(evtnm): null;
	}
	/** Adds the event handler for the specified event name.
	 */
	public void add(String evtnm, EventHandler evthd) {
		if (evtnm == null || evthd == null)
			throw new IllegalArgumentException("null");

		initEvthds();
		_evthds.put(evtnm, evthd);
	}
	/** Adds all event handlers of the specified map to this map.
	 */
	public void addAll(EventHandlerMap src) {
		if (src != null && !src.isEmpty()) {
			initEvthds();
			synchronized (_evthds) {
				synchronized (src._evthds) {
					_evthds.putAll(src._evthds);
				}
			}
		}
	}
	/** Initializes _evthds by creating and assigning a new map for it.
	 */
	private void initEvthds() {
		if (_evthds == null) {
			synchronized (this) {
				if (_evthds == null)
					_evthds = Collections.synchronizedMap(new HashMap(3));
			}
		}
	}

	//Cloneable//
	/** Clones this event handler map.
	 */
	public Object clone() {
		final EventHandlerMap clone;
		try {
			synchronized (this) {
				clone = (EventHandlerMap)super.clone();
			}
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}

		if (_evthds != null) {
			synchronized (_evthds) {
				clone._evthds =
					Collections.synchronizedMap(new HashMap(_evthds));
			}
		}
		return clone;
	}
	public String toString() {
		if (_evthds != null) {
			synchronized (_evthds) {
				return "[evthd:" + _evthds + ']';
			}
		}
		return "[evthd:]";
	}
}
