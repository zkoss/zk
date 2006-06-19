/* Events.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 22 15:49:51     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.event;

import java.util.Set;

import com.potix.lang.D;

import com.potix.zk.ui.Page;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.Executions;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.sys.ComponentCtrl;
import com.potix.zk.ui.sys.ExecutionsCtrl;
import com.potix.zk.ui.impl.EventProcessingThread;
import com.potix.zk.au.AuRequest;

/**
 * Utilities to handle events.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Events {
	private Events() {}

	/** Returns whether an event name is valid.
	 *
	 * <p>The event name must start with on and the third character
	 * must be upper case.
	 */
	public static final boolean isValid(String name) {
		return name != null && name.length() > 2
			&& name.charAt(0) == 'o' && name.charAt(1) == 'n'
			&& Character.isUpperCase(name.charAt(2));
	}

	/** Returns whether an event handler or listener is available for
	 * the specified component and event.
	 *
	 * <p>A event handler is either a public method named onXxx or
	 * a ZUL attribute named onXxx, where onXxx is the event name.
	 * A event listener is {@link EventListener} being added
	 * by {@link Component#addEventListener} and {@link Page#addEventListener}.
	 *
	 * @param asap whether to check only ASAP listener and handlers.
	 * See {@link Component#addEventListener} for more description.
	 */
	public static
	boolean isListenerAvailable(Component comp, String evtnm, boolean asap) {
		final String script =
			((ComponentCtrl)comp).getMillieu().getEventHandler(comp, evtnm);
		if (script != null)
			return true;

		if (ExecutionsCtrl.getEventMethod(comp.getClass(), evtnm) != null
		|| comp.isListenerAvailable(evtnm, asap))
			return true;

		if (!asap) {
			final Page page = comp.getPage();
			return page != null && page.isListenerAvailable(evtnm);
		}
		return false;
	}

	/** Sends the event to the specified component and process it
	 * immediately. This method can only be called when processing an event.
	 * It is OK to send event to component from another page as long as
	 * they are in the same desktop.
	 */
	public static void sendEvent(Component comp, Event event) {
		final Thread thd = (Thread)Thread.currentThread();
		if (!(thd instanceof EventProcessingThread))
			throw new UiException("Callable only when processing an event");
		try {
			((EventProcessingThread)thd).sendEvent(comp, event);
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	/** Posts an event.
	 */
	public static final void postEvent(Event event) {
		Executions.getCurrent().postEvent(event);
	}
	/** Posts a generic event (aka, an instance of {@link Event}).
	 */
	public static final void postEvent(String name, Component target, Object data) {
		postEvent(new Event(name, target, data));
	}
}
