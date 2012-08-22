/* Clients.java

	Purpose:
		
	Description:
		
	History:
		Fri May 26 14:25:06     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import java.util.List;
import java.util.Locale;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.out.*;

/**
 * Utilities to send {@link AuResponse} to the client.
 *
 * <p>Utilities here are mainly to control how the client (aka., the browser window)
 * behaves. To get the status, you might refer to {@link org.zkoss.zk.ui.event.ClientInfoEvent}.
 *
 * @author tomyeh
 * @see org.zkoss.zk.ui.event.ClientInfoEvent
 */
public class Clients {
	/** Sends an AU response ({@link AuResponse}) to the client.
	 * It is the same as <code>response(response.getOverrideKey(), response)</code>.
	 *
	 * @since 3.0.0
	 */
	public static final void response(AuResponse response) {
		Executions.getCurrent().addAuResponse(response);
	}
	/** Sends an AU response ({@link AuResponse}) to the client
	 * with the given key (instead of {@link AuResponse#getOverrideKey}).
	 *
	 * @param key could be anything. The second invocation of this method
	 * in the same execution with the same key will override the previous one.
	 * In other words, the previous one will be dropped.
	 * If null is specified, the response is simply appended to the end
	 * without overriding any previous one.
	 * @since 3.0.4
	 * @see #response
	 */
	public static final void response(String key, AuResponse response) {
		Executions.getCurrent().addAuResponse(key, response);
	}

	/** Asks the browser to confirm users whether to close the browser window.
	 *
	 * <p>If an non-null (non-empty) string is set, the browser will show up
	 * a confirmation dialog when an user tries to close the browser window,
	 * or browse to another URL.
	 * To reset (i.e., not showing any confirmation dialog), just call this
	 * method again with null.
	 *
	 * @param mesg the message to show when confirming users.
	 * If null (default) or emtpy, users can close the browser window directly.
	 */
	public static final void confirmClose(String mesg) {
		response(new AuConfirmClose(mesg));
	}

	/** Shows an error message at the browser.
	 * It works and looks similar to {@link org.zkoss.zul.Messagebox}.
	 * However, it is not customizable (at the server), but it is
	 * much faster and light-weighted.
	 * @since 5.0.3
	 */
	public static final void alert(String msg) {
		response(new AuAlert(msg));
	}
	/** Shows an error message at the browser.
	 * It is similar to {@link org.zkoss.zul.Messagebox}.
	 * @param msg the message to display.
	 * @param title the title of the message box
	 * @param icon the icon to show. It could null,
	 "QUESTION", "EXCLAMATION", "INFORMATION", "ERROR", "NONE".
	 * If null, "ERROR" is assumed
	 * @since 5.0.3
	 */
	public static final void alert(String msg, String title, String icon) {
		response(new AuAlert(msg, title, icon));
	}
	/** Shows an error message for the specified component, if any,
	 * at the browser.
	 * <p>You have to clear the error message manually with {@link #clearWrongValue}.
	 * @since 5.0.0
	 */
	public static final void wrongValue(Component comp, String msg) {
		response(new AuWrongValue(comp, msg));
	}
	/** Closes the error message of the specified component, if any,
	 * at the browser.
	 * @since 5.0.0
	 */
	public static final void clearWrongValue(Component comp) {
		response(new AuClearWrongValue(comp));
	}
	
	/** Closes the error message of the specified components, if any,
	 * at the browser.
	 * @since 5.0.0
	 */
	public static final void clearWrongValue(List<Component> comps) {
		response(new AuClearWrongValue(comps)); //append, not overwrite
	}
	/** Closes the error message of the specified components, if any,
	 * at the browser.
	 * @since 5.0.0
	 */
	public static final void clearWrongValue(Component[] comps) {
		response(new AuClearWrongValue(comps)); //append, not overwrite
	}
	
	/** Submits the form with the specified ID.
	 */
	public static final void submitForm(String formId) {
		response(new AuSubmitForm(formId));
	}
	/** Submits the form with the specified form.
	 * It assumes the form component is a HTML form.
	 */
	public static final void submitForm(Component form) {
		submitForm(form.getUuid());
	}
	/** Asks the client to print the current desktop (aka., browser window).
	 */
	public static void print() {
		response(new AuPrint());
	}

	/** Scrolls the ancestor elements to make the specified element visible.
	 * @since 3.6.1
	 */
	public static final void scrollIntoView(Component cmp) {
		response(new AuScrollIntoView(cmp));
	}
	/** Scrolls the current desktop (aka., browser window) by the specified number of pixels.
	 * If the number passed is positive, the desktop is scrolled down.
	 * If negative, it is scrolled up.
	 * @see #scrollTo
	 */
	public static final void scrollBy(int x, int y) {
		response(new AuScrollBy(x, y));
	}
	/** Scrolls the current desktop (aka., browser window) to the specified location (in pixels).
	 *
	 * @see #scrollBy
	 */
	public static final void scrollTo(int x, int y) {
		response(new AuScrollTo(x, y));
	}
	/** Resizes the current desktop (aka., browser window) by the specified number of pixels.
	 * If the numbers passed are positive, the desktop size is increased.
	 * Negative numbers reduce the size of the desktop.
	 *
	 * @see #resizeTo
	 */
	public static final void resizeBy(int x, int y) {
		response(new AuResizeBy(x, y));
	}
	/** Resizes the current desktop (aka., browser window) to the specified size (in pixels).
	 *
	 * @see #resizeBy
	 */
	public static final void resizeTo(int x, int y) {
		response(new AuResizeTo(x, y));
	}
	/** Moves the current desktop (aka., browser window) by the specified number of pixels.
	 * If the number passed is positive, the desktop is moved down.
	 * If negative, it is moved up.
	 * @see #moveTo
	 */
	public static final void moveBy(int x, int y) {
		response(new AuMoveBy(x, y));
	}
	/** Moves the current desktop (aka., browser window) to the specified location (in pixels).
	 *
	 * @see #moveBy
	 */
	public static final void moveTo(int x, int y) {
		response(new AuMoveTo(x, y));
	}

	/** Asks the browser to evaluate the specified JavaScript.
	 * <p>It has no effect if the client doesn't support JavaScript.
	 *
	 * @param javaScript the javaScript codes to run at the browser
	 */
	public static final void evalJavaScript(String javaScript) {
		response(new AuScript(null, javaScript));
	}
	
	/**
	 * Notification type: information
	 */
	public static final String NOTIFICATION_TYPE_INFO = "info";
	
	/**
	 * Notification type: warning
	 */
	public static final String NOTIFICATION_TYPE_WARNING = "warning";
	
	/**
	 * Notification type: error
	 */
	public static final String NOTIFICATION_TYPE_ERROR = "error";
	
	/**
	 * Shows a message at the center of the browser window.
	 * @param msg the message to show
	 * @since 6.0.1
	 */
	public static final void showNotification(String msg) {
		showNotification(msg, null, null, null, -1, false);
	}
	
	/**
	 * Shows a message at the center of the browser window.
	 * @param msg the message to show
	 * @param closable whether to close notification manually or not. If true there will be a
	 * close button on notification message and won't close until user click the button 
	 * or duration time up, default false.
	 * @since 6.5.0
	 */
	public static final void showNotification(String msg, boolean closable) {
		showNotification(msg, null, null, null, -1, closable);
	}
	
	/**
	 * Shows a message at the right side of the given component.
	 * @param msg the message to show
	 * @param ref the referenced component, null to be based on browser window
	 * @since 6.0.1
	 */
	public static final void showNotification(String msg, Component ref) {
		showNotification(msg, null, ref, null, -1, false);
	}
	
	/**
	 * Shows a message at the right side of the given component.
	 * @param msg the message to show
	 * @param ref the referenced component, null to be based on browser window
	 * @param closable whether to close notification manually or not. If true there will be a
	 * close button on notification message and won't close until user click the button 
	 * or duration time up, default false.
	 * @since 6.5.0
	 */
	public static final void showNotification(String msg, Component ref, boolean closable) {
		showNotification(msg, null, ref, null, -1, closable);
	}
	
	/**
	 * Displays a message.
	 * @param msg the message to show
	 * @param type available types are "info", "warning", "error"
	 * @param ref the referenced component, null to be based on browser window
	 * @param position predefined positions.
	 * <p> Available options are:
	 * <ul>
	 * 	<li><b>before_start</b><br/> the message appears above the anchor, aligned to the left.</li>
	 * 	<li><b>before_center</b><br/> the message appears above the anchor, aligned to the center.</li>
	 *  <li><b>before_end</b><br/> the message appears above the anchor, aligned to the right.</li>
	 *  <li><b>after_start</b><br/> the message appears below the anchor, aligned to the left.</li>
	 *  <li><b>after_center</b><br/> the message appears below the anchor, aligned to the center.</li>
	 *  <li><b>after_end</b><br/> the message appears below the anchor, aligned to the right.</li>
	 *  <li><b>start_before</b><br/> the message appears to the left of the anchor, aligned to the top.</li>
	 *  <li><b>start_center</b><br/> the message appears to the left of the anchor, aligned to the middle.</li>
	 *  <li><b>start_after</b><br/> the message appears to the left of the anchor, aligned to the bottom.</li>
	 *  <li><b>end_before</b><br/> the message appears to the right of the anchor, aligned to the top.</li>
	 *  <li><b>end_center</b><br/> the message appears to the right of the anchor, aligned to the middle.</li>
	 *  <li><b>end_after</b><br/> the message appears to the right of the anchor, aligned to the bottom.</li>
	 *  <li><b>overlap/top_left</b><br/> the message overlaps the anchor, with anchor and message aligned at top-left.</li>
	 *  <li><b>top_center</b><br/> the message overlaps the anchor, with anchor and message aligned at top-center.</li>
	 *  <li><b>overlap_end/top_right</b><br/> the message overlaps the anchor, with anchor and message aligned at top-right.</li>
	 *  <li><b>middle_left</b><br/> the message overlaps the anchor, with anchor and message aligned at middle-left.</li>
	 *  <li><b>middle_center</b><br/> the message overlaps the anchor, with anchor and message aligned at middle-center.</li>
	 *  <li><b>middle_right</b><br/> the message overlaps the anchor, with anchor and message aligned at middle-right.</li>
	 *  <li><b>overlap_before/bottom_left</b><br/> the message overlaps the anchor, with anchor and message aligned at bottom-left.</li>
	 *  <li><b>bottom_center</b><br/> the message overlaps the anchor, with anchor and message aligned at bottom-center.</li>
	 *  <li><b>overlap_after/bottom_right</b><br/> the message overlaps the anchor, with anchor and message aligned at bottom-right.</li>
	 *  <li><b>at_pointer</b><br/> the message appears with the upper-left aligned with the mouse cursor.</li>
	 *  <li><b>after_pointer</b><br/> the message appears with the top aligned with
	 *  	the bottom of the mouse cursor, with the left side of the message at the horizontal position of the mouse cursor.</li>
	 * </ul></p>
	 * @param duration the duration of notification in millisecond. If zero or 
	 * negative the notification does not dismiss until user left-clicks outside 
	 * of the notification box.
	 * @since 6.0.1
	 */
	public static final void showNotification(String msg, String type, 
			Component ref, String position, int duration) {
		showNotification(msg, type, ref, position, duration, false);
	}
	
	/**
	 * Displays a message.
	 * 
	 * @param msg the message to show
	 * @param type available types are "info", "warning", "error"
	 * @param ref the referenced component, null to be based on browser window
	 * @param position predefined positions.
	 * <p> Available options are:
	 * <ul>
	 * 	<li><b>before_start</b><br/> the message appears above the anchor, aligned to the left.</li>
	 * 	<li><b>before_center</b><br/> the message appears above the anchor, aligned to the center.</li>
	 *  <li><b>before_end</b><br/> the message appears above the anchor, aligned to the right.</li>
	 *  <li><b>after_start</b><br/> the message appears below the anchor, aligned to the left.</li>
	 *  <li><b>after_center</b><br/> the message appears below the anchor, aligned to the center.</li>
	 *  <li><b>after_end</b><br/> the message appears below the anchor, aligned to the right.</li>
	 *  <li><b>start_before</b><br/> the message appears to the left of the anchor, aligned to the top.</li>
	 *  <li><b>start_center</b><br/> the message appears to the left of the anchor, aligned to the middle.</li>
	 *  <li><b>start_after</b><br/> the message appears to the left of the anchor, aligned to the bottom.</li>
	 *  <li><b>end_before</b><br/> the message appears to the right of the anchor, aligned to the top.</li>
	 *  <li><b>end_center</b><br/> the message appears to the right of the anchor, aligned to the middle.</li>
	 *  <li><b>end_after</b><br/> the message appears to the right of the anchor, aligned to the bottom.</li>
	 *  <li><b>overlap/top_left</b><br/> the message overlaps the anchor, with anchor and message aligned at top-left.</li>
	 *  <li><b>top_center</b><br/> the message overlaps the anchor, with anchor and message aligned at top-center.</li>
	 *  <li><b>overlap_end/top_right</b><br/> the message overlaps the anchor, with anchor and message aligned at top-right.</li>
	 *  <li><b>middle_left</b><br/> the message overlaps the anchor, with anchor and message aligned at middle-left.</li>
	 *  <li><b>middle_center</b><br/> the message overlaps the anchor, with anchor and message aligned at middle-center.</li>
	 *  <li><b>middle_right</b><br/> the message overlaps the anchor, with anchor and message aligned at middle-right.</li>
	 *  <li><b>overlap_before/bottom_left</b><br/> the message overlaps the anchor, with anchor and message aligned at bottom-left.</li>
	 *  <li><b>bottom_center</b><br/> the message overlaps the anchor, with anchor and message aligned at bottom-center.</li>
	 *  <li><b>overlap_after/bottom_right</b><br/> the message overlaps the anchor, with anchor and message aligned at bottom-right.</li>
	 *  <li><b>at_pointer</b><br/> the message appears with the upper-left aligned with the mouse cursor.</li>
	 *  <li><b>after_pointer</b><br/> the message appears with the top aligned with
	 *  	the bottom of the mouse cursor, with the left side of the message at the horizontal position of the mouse cursor.</li>
	 * </ul></p>
	 * @param duration the duration of notification in millisecond. If zero or 
	 * negative the notification does not dismiss until user left-clicks outside 
	 * of the notification box.
	 * @param closable whether to close notification manually or not. If true there will be a
	 * close button on notification message and won't close until user click the button 
	 * or duration time up, default false.
	 * @since 6.5.0
	 */
	public static final void showNotification(String msg, String type,
			Component ref, String position, int duration, boolean closable) {
		Execution exec = Executions.getCurrent();
		Page page = ref != null ? ref.getPage() : null;
		if (page == null && exec instanceof ExecutionCtrl)
			page = ((ExecutionCtrl) exec).getCurrentPage();
		if (type == null)
			type = NOTIFICATION_TYPE_INFO;
		response(new AuNotification(msg, type, page, ref, position, duration, closable));
	}
	
	/**
	 * Displays a message.
	 * @param msg the message to show
	 * @param type available types are "info", "warning", "error"
	 * @param ref the referenced component, null to be based on browser window
	 * @param x the horizontal position of the notification, aligned at top-left (in pixel)
	 * @param y the vertical position of the notification, aligned at top-left (in pixel)
	 * @param duration the duration of notification in millisecond. If zero or 
	 * negative the notification does not dismiss until user left-clicks outside 
	 * of the notification box.
	 * @since 6.0.1
	 */
	public static final void showNotification(String msg, String type, 
			Component ref, int x, int y, int duration) {
		showNotification(msg, type, ref, x, y, duration, false);
	}
	
	/**
	 * Displays a message.
	 * @param msg the message to show
	 * @param type available types are "info", "warning", "error"
	 * @param ref the referenced component, null to be based on browser window
	 * @param x the horizontal position of the notification, aligned at top-left (in pixel)
	 * @param y the vertical position of the notification, aligned at top-left (in pixel)
	 * @param duration the duration of notification in millisecond. If zero or 
	 * negative the notification does not dismiss until user left-clicks outside 
	 * of the notification box.
	 * @param closable whether to close notification manually or not. If true there will be a
	 * close button on notification message and won't close until user click the button 
	 * or duration time up, default false.
	 * @since 6.5.0
	 */
	public static final void showNotification(String msg, String type, 
			Component ref, int x, int y, int duration, boolean closable) {
		Execution exec = Executions.getCurrent();
		Page page = ref != null ? ref.getPage() : null;
		if (page == null && exec instanceof ExecutionCtrl)
			page = ((ExecutionCtrl) exec).getCurrentPage();
		if (type == null)
			type = NOTIFICATION_TYPE_INFO;
		response(new AuNotification(msg, type, page, ref, x, y, duration, closable));
	}
	
	/** Shows the busy message at the browser such that
	 * the user knows the system is busy.
	 *
	 * <p>It is usually used with {@link org.zkoss.zk.ui.event.Events#echoEvent}
	 * to prevent the user to click another buttons or components.
	 *
	 * <p>To cover only a particular component, use {@link #showBusy(Component, String)}.
	 * To close, use {@link #clearBusy()}.
	 *
	 * @param msg the message to show. If null, the default message (processing)
	 * is shown.
	 * @see #clearBusy()
	 * @since 5.0.0
	 */
	public static final void showBusy(String msg) {
		response(new AuShowBusy(msg));
	}
	/** Cleans the busy message at the browser.
	 * @see #showBusy(String)
	 * @since 5.0.0
	 */
	public static final void clearBusy() {
		response(new AuClearBusy());
	}
	/** Shows the busy message at the browser that covers only the specified
	 * component.
	 * It is used to denote a portion of the desktop is busy, and
	 * the user still can access the other part.
	 * In other words, it means there is a long operation taking place.
	 * <p>To execute a long operation asynchronously, the developer can use
	 * a working thread,
	 * or use {@link org.zkoss.zk.ui.event.EventQueue#subscribe(org.zkoss.zk.ui.event.EventListener,boolean)}.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Developer%27s_Reference/UI_Patterns/Long_Operations">Long Operations</a>
	 *
	 * @param comp the component that the busy message to cover.
	 * Ignored if null. Notice that if the component is not found,
	 * the busy message won't be shown. In additions, the busy message
	 * is removed automatically if the component is detached later.
	 * To manually remove the busy message, use {@link #clearBusy(Component)}
	 * @param msg the message to show. If null, the default message (processing)
	 * is shown.
	 * @see #clearBusy(Component)
	 * @since 5.0.0
	 */
	public static final void showBusy(Component comp, String msg) {
		response(new AuShowBusy(comp, msg));
	}
	/** Clears the busy message at the browser that covers only the specified
	 * component.
	 * @param comp the component that the busy message to cover.
	 * @see #showBusy(Component, String)
	 * @since 5.0.0
	 */
	public static final void clearBusy(Component comp) {
		response(new AuClearBusy(comp));
	}

	/** Forces the client to re-calculate the size of the given component.
	 * For better performance, ZK Client Engine will cache the size of
	 * components with hflex=min or vflex=min, and recalculate it only
	 * necessary. However, sometimes it is hard (too costly) to know
	 * if the size of a component with flex=min is changed. For example,
	 * If another component is added to it and causes the size changed.
	 * In this case, you could use this method to enforce the re-calculation.
	 * @since 5.0.8
	 */
	public static final void resize(Component comp) {
		response(new AuResizeWidget(comp));
	}

	/** Reloads the client-side messages in the specified locale.
	 * It is used if you allow the user to change the locale dynamically.
	 *
	 * <p>Notice that this method only reloads the <i>standard</i> messages.
	 * The application has to update the component's content (such as labels)
	 * manually if necessary.
	 *
	 * <p>Limitation: it reloads only the messages of ZK Client Engine
	 * and ZUL components. It does not reload messages loaded by your
	 * own JavaScript codes.
	 *
	 * @param locale the locale. If null, {@link org.zkoss.util.Locales#getCurrent}
	 * is assumed.
	 * @exception UnsupportedOperationException if the device is not ajax.
	 * @since 3.6.3
	 */
	public static final void reloadMessages(Locale locale)
	throws java.io.IOException {
		Executions.getCurrent().getDesktop().getDevice().reloadMessages(locale);
	}

	/** Logs the message to the client.
	 *  <p>data[0]: the title
	 * @since 5.0.8
	 */
	public static final void log(String msg) {
		response(new AuLog(msg));
	}
	
}
