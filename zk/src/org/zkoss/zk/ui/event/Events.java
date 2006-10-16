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
package org.zkoss.zk.ui.event;

import java.util.Set;

import org.zkoss.lang.D;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.impl.EventProcessingThread;
import org.zkoss.zk.au.AuRequest;

/**
 * Utilities to handle events.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Events {
	private Events() {} //prevent from creation

	/** The onClick event (used with {@link MouseEvent}).
	 */
	public static final String ON_CLICK = "onClick";
	/** The onRightClick event (used with {@link MouseEvent}).
	 */
	public static final String ON_RIGHT_CLICK = "onRightClick";
	/** The onDoubleClick event (used with {@link MouseEvent}).
	 */
	public static final String ON_DOUBLE_CLICK = "onDoubleClick";
	/** The onOK event (used with {@link KeyEvent}).
	 */
	public static final String ON_OK = "onOK";
	/** The onCancel event (used with {@link KeyEvent}).
	 */
	public static final String ON_CANCEL = "onCancel";
	/** The onCtrlKey event (used with {@link KeyEvent}).
	 */
	public static final String ON_CTRL_KEY = "onCtrlKey";
	/** The onChange event (used with {@link InputEvent}).
	 */
	public static final String ON_CHANGE = "onChange";
	/** The onChanging event (used with {@link InputEvent}).
	 */
	public static final String ON_CHANGING = "onChanging";
	/** The onError event (used with {@link ErrorEvent}).
	 */
	public static final String ON_ERROR = "onError";
	/** The onScroll event (used with {@link ScrollEvent}).
	 */
	public static final String ON_SCROLL = "onScroll";
	/** The onScrolling event (used with {@link ScrollEvent}).
	 */
	public static final String ON_SCROLLING = "onScrolling";
	/** The onSelect event (used with {@link SelectEvent}).
	 */
	public static final String ON_SELECT = "onSelect";
	/** The onCheck event (used with {@link CheckEvent}).
	 */
	public static final String ON_CHECK = "onCheck";
	/** The onMove event (used with {@link MoveEvent}).
	 */
	public static final String ON_MOVE = "onMove";
	/** The onZIndex event (used with {@link ZIndexEvent}).
	 */
	public static final String ON_Z_INDEX = "onZIndex";
	/** The onOpen event (used with {@link OpenEvent}).
	 */
	public static final String ON_OPEN = "onOpen";
	/** The onShow event (used with {@link ShowEvent}).
	 */
	public static final String ON_SHOW = "onShow";
	/** The onClose event (used with {@link Event})
	 * used to denote the close button is pressed.
	 */
	public static final String ON_CLOSE = "onClose";
	/** The onRender event (used with {@link org.zkoss.zk.ui.ext.Render}).
	 */
	public static final String ON_RENDER = "onRender";
	/** The onTimer event (used with {@link Event}).
	 * Sent when a timer is up.
	 */
	public static final String ON_TIMER = "onTimer";
	/** The onFocus event (used with {@link Event}).
	 * Sent when a component gets a focus.
	 */
	public static final String ON_FOCUS = "onFocus";
	/** The onBlur event (used with {@link Event}).
	 * Sent when a component loses a focus.
	 */
	public static final String ON_BLUR = "onBlur";
	/** The onDrop event (used with {@link DropEvent}).
	 * Sent when a component is dragged and drop to another.
	 */
	public static final String ON_DROP = "onDrop";
	/** The onNotify event (used with {@link Event}).
	 * It is not used by any component, but it is, rather, designed to
	 * let users add customized events.
	 */
	public static final String ON_NOTIFY = "onNotify";
	/** The onSort event (used with {@link Event})
	 * to notify a request for sorting.
	 */
	public static final String ON_SORT = "onSort";
	/** The onBookmarkChanged event (used with {@link BookmarkEvent})
	 * to notify that user pressed BACK, FORWARD or specified URL directly
	 * that causes the bookmark is changed (but still in the same desktop).
	 * <p>All root components of all pages of the desktop will
	 * recieve this event.
	 */
	public static final String ON_BOOKMARK_CHANGED = "onBookmarkChanged";
	/** The onClientInfo event (used with {@link ClientInfoEvent}).
	 */
	public static final String ON_CLIENT_INFO = "onClientInfo";

	/** The onCreate event (used with {@link CreateEvent}) to notify a compoent
	 * that it (and its children) has been created by ZK's evaluating a ZUML page.
	 */
	public static final String ON_CREATE = "onCreate";
	/** The onModal event (used with {@link Event}) to notify a component
	 * shall become modal. Currently, only ZUL's window components support it.
	 */
	public static final String ON_MODAL = "onModal";

	/** The onUser event. It is a generic event that an application developer
	 * might send from the client. ZK doesn't use this event.
	 */
	public static final String ON_USER = "onUser";

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

	/** Returns whether the current thread is an event listener.
	 */
	public static final boolean inEventListener() {
		return Thread.currentThread() instanceof EventProcessingThread;
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
			((ComponentCtrl)comp).getMilieu().getEventHandler(comp, evtnm);
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
		if (name == null || name.length() == 0 || target == null)
			throw new IllegalArgumentException("null");
		postEvent(new Event(name, target, data));
	}
}
