/* Events.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 22 15:49:51     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import java.lang.reflect.Method;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.EventProcessingThread;
import org.zkoss.zk.ui.impl.EventProcessor;
import org.zkoss.zk.au.out.AuEcho;
import org.zkoss.zk.ui.util.Clients;

/**
 * Utilities to handle events.
 *
 * @author tomyeh
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
	/** The onSelection event (used with {@link SelectionEvent}).
	 */
	public static final String ON_SELECTION = "onSelection";
	/** The onCheck event (used with {@link CheckEvent}).
	 */
	public static final String ON_CHECK = "onCheck";
	/** The onMove event (used with {@link MoveEvent}).
	 */
	public static final String ON_MOVE = "onMove";
	/** The onSize event (used with {@link SizeEvent}).
	 */
	public static final String ON_SIZE = "onSize";
	/** The onZIndex event (used with {@link ZIndexEvent}).
	 */
	public static final String ON_Z_INDEX = "onZIndex";
	/** The onOpen event (used with {@link OpenEvent}).
	 */
	public static final String ON_OPEN = "onOpen";
	/** The onClose event (used with {@link Event})
	 * used to denote the close button is pressed.
	 */
	public static final String ON_CLOSE = "onClose";
	/** The onRender event (used with {@link org.zkoss.zk.ui.ext.client.RenderOnDemand}).
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
	/** The onUpload event (used with {@link UploadEvent}.
	 */
	public static final String ON_UPLOAD = "onUpload";
	/** The onBookmarkChange event (used with {@link BookmarkEvent})
	 * to notify that user pressed BACK, FORWARD or specified URL directly
	 * that causes the bookmark is changed (but still in the same desktop).
	 * <p>All root components of all pages of the desktop will
	 * recieve this event.
	 * @since 3.0.8
	 */
	public static final String ON_BOOKMARK_CHANGE = "onBookmarkChange";
	/** @deprecated As of release 3.0.8, replaced with {@link #ON_BOOKMARK_CHANGE}.
	 */
	public static final String ON_BOOKMARK_CHANGED = "onBookmarkChanged";
	/** The onURIChange event (used with {@link URIEvent})
	 * to notify that the associated URI of a component is changed.
	 * Currently only the iframe component supports this event.
	 * @since 3.5.0
	 */
	public static final String ON_URI_CHANGE = "onURIChange";
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
	/** The onPiggyback event (used with {@link Event}) used to notify
	 * a root component that the client has sent a request to the server.
	 * It is meaningful only if it is registered to the root component.
	 * Once registered, it is called
	 * each time the client sends a request to the server.
	 * The onPiggyback's event listener is processed after all other
	 * events are processed.
	 *
	 * <p>The onPiggyback event is designed to let developers piggyback
	 * the least-emergent UI updates to the client.
	 *
	 * @since 2.4.0
	 */
	public static final String ON_PIGGYBACK = "onPiggyback";

	/** The onUser event. It is a generic event that an application developer
	 * might send from the client. ZK doesn't use this event.
	 */
	public static final String ON_USER = "onUser";

	/** The onMinimize event (used with {@link MinimizeEvent}).
	 * @since 3.5.0
	 */
	public static final String ON_MINIMIZE = "onMinimize";

	/** The onMaximize event (used with {@link MaximizeEvent}).
	 * @since 3.5.0
	 */
	public static final String ON_MAXIMIZE = "onMaximize";

	/** The onFulfill event (used with {@link FulfillEvent})
	 * to denote a fulfill condition has been applied.
	 * Developer usually listens to this event to process the
	 * new created children. For example, you might invoke
	 * {@link org.zkoss.zk.ui.Components#wireFellows} to process these new components.
	 * @since 3.0.8
	 */
	public static final String ON_FULFILL = "onFulfill";

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
		return EventProcessor.inEventListener();
	}

	/** Returns whether an event handler or listener is available for
	 * the specified component and event.
	 *
	 * <p>A event handler is either a public method named onXxx or
	 * a ZUL attribute named onXxx, where onXxx is the event name.
	 * A event listener is {@link EventListener} being added
	 * by {@link Component#addEventListener} and {@link Page#addEventListener}.
	 *
	 * <p>Unlike {@link Component#isListenerAvailable}, which checks
	 * only the event listener, this method
	 * check both event handlers and listeners, i.e.,
	 * the onXxx members defined in ZUML, the onXxx method defined
	 * in the implementation class, and the event listener registered.
	 *
	 * @param asap whether to check only non-deferrable listener,
	 * i.e., not implementing {@link org.zkoss.zk.ui.event.Deferrable},
	 * or {@link org.zkoss.zk.ui.event.Deferrable#isDeferrable} is false.
	 * @see org.zkoss.zk.ui.event.Deferrable
	 * @see Component#isListenerAvailable
	 */
	public static
	boolean isListened(Component comp, String evtnm, boolean asap) {
		if (((ComponentCtrl)comp).getEventHandler(evtnm) != null)
			return true;

		if (ComponentsCtrl.getEventMethod(comp.getClass(), evtnm) != null
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
		final Execution exec = Executions.getCurrent();
		final Desktop desktop = exec.getDesktop();
			//note: we don't use comp.getDesktop because 1) it may be null
			//2) it may be different from the current desktop

		event = ((DesktopCtrl)desktop).beforeSendEvent(event);
		if (event == null)
			return; //done

		final Thread thd = (Thread)Thread.currentThread();
		if (!(thd instanceof EventProcessingThread)) {
			if (!desktop.getWebApp().getConfiguration().isEventThreadEnabled()) {
				final ExecutionCtrl execCtrl = (ExecutionCtrl)exec;
				final Page page = execCtrl.getCurrentPage();
				final EventProcessor proc =
					new EventProcessor(desktop, comp, event);
				proc.setup();
				try {
					proc.process();
				} catch (Exception ex) {
					throw UiException.Aide.wrap(ex);
				} finally {
					execCtrl.setCurrentPage(page);
				}
				return; //done
			}

			throw new UiException("Callable only in the event listener");
		}

		try {
			((EventProcessingThread)thd).sendEvent(comp, event);
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	/** Sends the event the target specified in the event.
	 *
	 * <p>Note: {@link Event#getTarget} cannot be null.
	 */
	public static void sendEvent(Event event) {
		sendEvent(event.getTarget(), event);
	}

	/** Posts an event to the current execution.
	 *
	 * <p>The priority of the event is assumed to be 0. Refer to
	 * {@link #postEvent(int, Event)}.
	 *
	 * <p>On the other hand, the event sent by {@link #sendEvent} is processed
	 * immediately without posting it to the queue.
	 *
	 * <p>Note: if the target of an event is not attached to
	 * the page yet, the event is ignored silently.
	 * @see #sendEvent
	 * @see #echoEvent
	 * @see #postEvent(int, Event)
	 */
	public static final void postEvent(Event event) {
		Executions.getCurrent().postEvent(event);
	}
	/** Posts an instance of {@link Event} to the current execution.
	 * <p>The priority of the event is assumed to be 0. Refer to
	 * {@link #postEvent(int, String, Component, Object)}.
	 * @see #postEvent(Event)
	 * @see #postEvent(int, String, Component, Object)
	 */
	public static final
	void postEvent(String name, Component target, Object data) {
		postEvent(0, name, target, data);
	}
	/** Posts an event to the current execution with the specified priority.
	 *
	 * <p>The posted events are processed from the higher priority to the 
	 * lower one. If two events are posted with the same priority,
	 * the earlier the event being posted is processed earlier
	 * (first-in-first-out).
	 *
	 * <p>The priority posted by posted by {@link #postEvent(Event)} is
	 * 0.
	 * Applications shall not use the priority higher than 10,000 and
	 * lower than -10,000 since they are reserved for component
	 * development.
	 *
	 * @param priority the priority of the event.
	 * @since 3.0.7
	 */
	public static final void postEvent(int priority, Event event) {
		Executions.getCurrent().postEvent(priority, event);
	}
	/** Posts an instance of {@link Event} to the current execution
	 * with the specified priority.
	 *
	 * <p>The posted events are processed from the higher priority to the 
	 * lower one. If two events are posted with the same priority,
	 * the earlier the event being posted is processed earlier
	 * (first-in-first-out).
	 *
	 * <p>The priority posted by posted by {@link #postEvent(Event)} is
	 * 0.
	 * Applications shall not use the priority higher than 10,000 and
	 * lower than -10,000 since they are reserved for component
	 * development.
	 *
	 * @param priority the priority of the event.
	 * @since 3.0.7
	 */
	public static final void postEvent(int priority,
	String name, Component target, Object data) {
		if (name == null || name.length() == 0 || target == null)
			throw new IllegalArgumentException("null");
		postEvent(priority, new Event(name, target, data));
	}

	/** Echos an event.
	 * By echo we mean the event is fired after the client receives the AU
	 * responses and then echoes back.
	 * In others, the event won't be execute in the current execution.
	 * Rather, it executes after the client receives the AU responses
	 * and then echoes back the event back.
	 *
	 * <p>It is usually if you want to prompt the user before doing a long
	 * operartion. A typical case is to open a hightlighted window to
	 * prevent the user from clicking any button before the operation gets done.
	 *
	 * @since 3.0.2
	 * @see #sendEvent
	 * @see #echoEvent
	 * @param name the event name, such as onSomething
	 * @param target the component to receive the event (never null).
	 * @param data the extra information, or null if not available.
	 * It will become {@link Event#getData}.
	 */
	public static final void echoEvent(String name, Component target, String data) {
		if (name == null || name.length() == 0 || target == null)
			throw new IllegalArgumentException();

		Clients.response(new AuEcho(target, name, data));
	}

	/** <p>Add onXxx event handler defined in controller object to the specified 
	 * component. 
	 * The controller is a POJO file with onXxx methods(the event handler codes). 
	 * This utility method registers these onXxx events to the specified
	 * component so you don't have to implement and add {@link EventListener}
	 * into the component one by one.</p>
	 * 
	 * <p>All public methods whose names start with "on" in controller object 
	 * are considered
	 * as event handlers and the corresponding event is listened.
	 * For example, if the controller object has a method named onOK,
	 * then the onOK event is listened and the onOK method is called
	 * when the event is received.
	 * <p>Since 3.0.8, this method treats ForwardEvent specially. If the 
	 * event argument going to be passed into the onXxx event listener is a 
	 * ForwardEvent and the onXxx event listener defined in the controller 
	 * specifies a specific event class as its parameter rather than generic 
	 * Event or ForwardEvent class, then this method will unwrap the 
	 * ForwardEvent automatically 
	 * (see {@link org.zkoss.zk.ui.event.ForwardEvent#getOrigin()})
	 * and pass the original forwarded event to the defined onXxx event listener.</p>
	 * <p>This is a useful tool for MVC design practice. You can write
	 * onXxx event handler codes in controller object and use this utility to
	 * register the events to the specified component.</p>
	 * 
	 * @param comp the component to be registered the events 
	 * @param controller a POJO file with onXxx methods(event handlers) 
	 * @since 3.0.6
	 * @see GenericEventListener
	 */
	public static final void addEventListeners(Component comp, final Object controller) {
		final GenericEventListener evtl = new GenericEventListener() {
			protected Object getController() {
				return controller;
			}
		};
		evtl.bindComponent(comp);
	}

	/** Returns the real origin event of a forwarded event.
	 * By real we mean the last non-forward event in the chain of
	 * {@link ForwardEvent#getOrigin}.
	 * Notice that a forward event might be forwarded again, so
	 * {@link ForwardEvent#getOrigin} might not be the real origin.
	 * @since 3.5.1
	 */
	public static final Event getRealOrigin(ForwardEvent event) {
		for (;;) {
			Event evt = event.getOrigin();
			if (!(evt instanceof ForwardEvent))
				return evt;
			event = (ForwardEvent)evt;
		}
	}
}
