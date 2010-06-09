/* EventQueueProviderImpl.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul 23 19:36:16     2009, Created by tomyeh

	Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.event.impl;

import java.util.Map;
import java.util.HashMap;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.event.*;

/**
 * A simple implementation of {@link EventQueueProvider}.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class EventQueueProviderImpl implements EventQueueProvider {
	/** The attribute used to store the map of event queues.
	 */
	protected static final String ATTR_EVENT_QUEUES = "org.zkoss.zk.ui.event.eventQueues";

	public EventQueue lookup(String name, String scope, boolean autoCreate) {
		final Execution exec = Executions.getCurrent();
		if (exec == null)
			throw new IllegalStateException("Not in an execution");

		final boolean bAppScope = EventQueues.APPLICATION.equals(scope);
		if (bAppScope || EventQueues.SESSION.equals(scope)) {
			return lookup0(name, bAppScope ?
				(Scope)exec.getDesktop().getWebApp(): exec.getSession(), autoCreate);
		} else  if (EventQueues.DESKTOP.equals(scope)) {
			final Desktop desktop = exec.getDesktop();
			Map eqs = (Map)desktop.getAttribute(ATTR_EVENT_QUEUES);
			if (eqs == null)
				desktop.setAttribute(ATTR_EVENT_QUEUES, eqs = new HashMap(4));

			EventQueue eq = (EventQueue)eqs.get(name);
			if (autoCreate && eq == null)
				eqs.put(name, eq = new DesktopEventQueue());
			return eq;
		} else
			throw new UnsupportedOperationException("Unknown scope: "+scope);
	}
	public EventQueue lookup(String name, Session sess, boolean autoCreate) {
		return lookup0(name, sess, autoCreate);
	}
	public EventQueue lookup(String name, WebApp wapp, boolean autoCreate) {
		return lookup0(name, wapp, autoCreate);
	}
	/** Looks up a session or application scoped event queue. */
	private EventQueue lookup0(String name, Scope ctxscope, boolean autoCreate) {
		Map eqs;
		synchronized (ctxscope) {
			eqs = (Map)ctxscope.getAttribute(ATTR_EVENT_QUEUES);
			if (eqs == null)
				ctxscope.setAttribute(ATTR_EVENT_QUEUES, eqs = new HashMap(4));
		}

		EventQueue eq;
		synchronized (eqs) {
			eq = (EventQueue)eqs.get(name);
			if (autoCreate && eq == null)
				eqs.put(name, eq = new ServerPushEventQueue());
		}
		return eq;
	}
	public boolean remove(String name, String scope) {
		final Execution exec = Executions.getCurrent();
		if (exec == null)
			throw new IllegalStateException("Not in an execution");

		final boolean bAppScope = EventQueues.APPLICATION.equals(scope);
		if (bAppScope || EventQueues.SESSION.equals(scope)) {
			final Scope ctxscope = bAppScope ?
				(Scope)exec.getDesktop().getWebApp(): exec.getSession();
			Map eqs = (Map)ctxscope.getAttribute(ATTR_EVENT_QUEUES);
			if (eqs != null) {
				EventQueue eq;
				synchronized (eqs) {
					eq = (EventQueue)eqs.remove(name);
				}
				if (eq != null) {
					eq.close();
					return true;
				}
			}
			return false;
		} else  if (EventQueues.DESKTOP.equals(scope)) {
			final Desktop desktop = exec.getDesktop();
			Map eqs = (Map)desktop.getAttribute(ATTR_EVENT_QUEUES);
			if (eqs != null) {
				EventQueue eq = (EventQueue)eqs.remove(name);
				if (eq != null) {
					eq.close();
					return true;
				}
			}
		}
		return false;
	}
}
