/* EventInterceptors.java

	Purpose:
		
	Description:
		
	History:
		Mon Oct 15 22:08:52     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

import org.zkoss.io.Serializables;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.EventInterceptor;

/**
 * Utilities used to handle {@link EventInterceptor}.
 *
 * <p>Thread safe.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class EventInterceptors implements Cloneable, java.io.Serializable {
	/* A list of {@link EventInterceptor}. */
	private transient EventInterceptor[] _eis;

	/** Adds an event interceptor.
	 */
	public void addEventInterceptor(EventInterceptor ei) {
		if (ei == null)
			throw new IllegalArgumentException();

		//For better performance (of invoking beforeSendEvent and others
		//we make a copy first
		final List eilst = new LinkedList();
		final EventInterceptor[] eis = _eis;
		if (eis != null) {
			for (int j = 0; j < eis.length; ++j)
				eilst.add(eis[j]);
		}
		eilst.add(ei);

		_eis = (EventInterceptor[])
			eilst.toArray(new EventInterceptor[eilst.size()]);
	}
	/** Removes an event interceptor.
	 *
	 * <p>Note: we use the equals method to test whether
	 * two interceptors are the same.
	 *
	 * @return whether the listener is removed successfully.
	 */
	public boolean removeEventInterceptor(EventInterceptor ei) {
		if (ei != null && _eis != null) {
			final List eilst = new LinkedList();
			final EventInterceptor[] eis = _eis;
			boolean found = false;
			for (int j = 0; j < eis.length; ++j) {
				if (!found && eis[j].equals(ei))
					found = true;
				else
					eilst.add(eis[j]);
			}

			if (found) {
				final int sz = eilst.size();
				_eis = sz == 0 ? null:
					(EventInterceptor[])eilst.toArray(new EventInterceptor[sz]);
				return true;
			}
		}
		return false;
	}
	/** Removes an event interceptor with the specified class.
	 *
	 * <p>Note: we tests whether an interceptor is an instance of
	 * the specified class. At most one instance is removed.
	 *
	 * @return whether the listener is removed successfully.
	 */
	public boolean removeEventInterceptor(Class klass) {
		if (klass != null && _eis != null) {
			final List eilst = new LinkedList();
			final EventInterceptor[] eis = _eis;
			boolean found = false;
			for (int j = 0; j < eis.length; ++j) {
				if (!found && klass.isInstance(eis[j]))
					found = true;
				else
					eilst.add(eis[j]);
			}

			if (found) {
				final int sz = eilst.size();
				_eis = sz == 0 ? null:
					(EventInterceptor[])eilst.toArray(new EventInterceptor[sz]);
				return true;
			}
		}
		return false;
	}
	/** Invokes {@link EventInterceptor#beforeSendEvent}
	 */
	public Event beforeSendEvent(Event event) {
		final EventInterceptor[] eis = _eis;
		if (eis != null) {
			for (int j = 0; j < eis.length; ++j) {
				event = eis[j].beforeSendEvent(event);
				if (event == null)
					return null; //done
			}
		}
		return event;
	}
	/** Invokes {@link EventInterceptor#beforePostEvent}
	 */
	public Event beforePostEvent(Event event) {
		final EventInterceptor[] eis = _eis;
		if (eis != null) {
			for (int j = 0; j < eis.length; ++j) {
				event = eis[j].beforePostEvent(event);
				if (event == null)
					return null; //done
			}
		}
		return event;
	}
	/** Invokes {@link EventInterceptor#beforeProcessEvent}
	 */
	public Event beforeProcessEvent(Event event) {
		final EventInterceptor[] eis = _eis;
		if (eis != null) {
			for (int j = 0; j < eis.length; ++j) {
				event = eis[j].beforeProcessEvent(event);
				if (event == null)
					return null; //done
			}
		}
		return event;
	}
	/** Invokes {@link EventInterceptor#afterProcessEvent}
	 */
	public void afterProcessEvent(Event event) {
		final EventInterceptor[] eis = _eis;
		if (eis != null) {
			for (int j = 0; j < eis.length; ++j)
				eis[j].afterProcessEvent(event);
		}
	}

	//Cloneable//
	public Object clone() {
		final EventInterceptors clone;
		try {
			clone = (EventInterceptors)super.clone();
			if (clone._eis != null)
				clone._eis = (EventInterceptor[])clone._eis.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
		return clone;
	}

	//Serializable//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		Serializables.smartWrite(s, _eis);
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		final Collection eilst = Serializables.smartRead(s, (Collection)null);
		if (eilst != null)
			_eis = (EventInterceptor[])
				eilst.toArray(new EventInterceptor[eilst.size()]);
	}
}
