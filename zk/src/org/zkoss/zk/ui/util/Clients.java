/* Clients.java

	Purpose:
		
	Description:
		
	History:
		Fri May 26 14:25:06     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.io.InputStream;
import java.io.IOException;

import org.zkoss.util.Locales;
import org.zkoss.io.Files;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.http.Wpds;
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
	/** Sends an AU response ({@link AuResponse})to the client
	 * with response's command ({@link AuResponse#getCommand}) as the key.
	 *
	 * <p>Since response's command is used as the key, so the second
	 * invocation of this method with a response that has the same command
	 * will override the previous one. For example, the first one will be
	 * ignored since both have the same command.
	 * <pre><code>
	 *	response(new AuShowBusy("this will have no effect", true));
	 *	response(new AuShowBusy(null, false));</code></pre>
	 *
	 * <p>If this is an issue, use {@link #response(String, AuResponse)}
	 * instead.
	 *
	 * @since 3.0.0
	 */
	public static final void response(AuResponse response) {
		Executions.getCurrent()
			.addAuResponse(response.getCommand(), response);
	}
	/** Sends an AU response ({@link AuResponse}) to the client
	 * with the specified key.
	 *
	 * @param key could be anything. The second invocation of this method
	 * in the same execution with the same key will override the previous one.
	 * In other words, the previous one will be dropped.
	 * If null is specified, the response is simply appended to the end
	 * without overriding any previous one.
	 * @since 3.0.4
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
	public static final void clearWrongValue(List comps) {
		response(null, new AuClearWrongValue(comps)); //append, not overwrite
	}
	/** Closes the error message of the specified components, if any,
	 * at the browser.
	 * @since 5.0.0
	 */
	public static final void clearWrongValue(Component[] comps) {
		response(null, new AuClearWrongValue(comps)); //append, not overwrite
	}
	/** @deprecated As of release 5.0.0, replaced with {@link #clearWrongValue(Component)}.
	 */
	public static final void closeErrorBox(Component comp) {
		clearWrongValue(comp);
	}
	/** @deprecated As of release 5.0.0, replaced with {@link #clearWrongValue(List)}.
	 */
	public static final void closeErrorBox(List comps) {
		clearWrongValue(comps);
	}
	/** @deprecated As of release 5.0.0, replaced with {@link #clearWrongValue(Component[])}.
	 */
	public static final void closeErrorBox(Component[] comps) {
		clearWrongValue(comps);
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

	/** Shows the busy message at the brower such that
	 * the user knows the system is busy.
	 *
	 * <p>It is usually used with {@link org.zkoss.zk.ui.event.Events#echoEvent}
	 * to prevent the user to click another buttons or components.
	 *
	 * @param msg the message to show. If null, the default message (processing)
	 * is shown. It is ignored if the open argument is false.
	 * @param open whether to open or to close the busy message.
	 * @since 3.0.2
	 */
	public static final void showBusy(String msg, boolean open) {
		response(new AuShowBusy(msg, open));
	}
	/** Shows the busy message at the browser that covers only the specified
	 * component.
	 * It is used to denote a portion of the desktop is busy, and
	 * the user still can access the other part.
	 * In other words, it means there is a long operation taking place.
	 * <p>To execute a long operation asynchronously, the developer can use
	 * a working thread,
	 * or use {@link org.zkoss.zk.ui.event.EventQueue#subscribe(org.zkoss.zk.ui.event.EventListener,boolean)}.
	 * <p>See also <a href="http://docs.zkoss.org/wiki/Long_Operations">Long Operations</a>
	 *
	 * @param comp the component that the busy message to cover.
	 * Ignored if null. Notice that if the component is not found,
	 * the busy message won't be shown. In additions, the busy message
	 * is removed automatically if the component is detached later.
	 * @param msg the message to show. If null, the default message (processing)
	 * is shown. It is ignored if the open argument is false.
	 * @param open whether to open or to close the busy message.
	 * @since 5.0.0
	 */
	public static final void showBusy(Component comp, String msg, boolean open) {
		response(new AuShowBusy(comp, msg, open));
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
	 * @param locale the locale. If null, {@link Locales#getCurrent}
	 * is assumed.
	 * @since 3.6.3
	 */
	public static final void reloadMessages(Locale locale)
	throws IOException {
		if (locale == null)
			locale = Locales.getCurrent();

		final StringBuffer sb = new StringBuffer(4096);
		final Locale oldl = Locales.setThreadLocal(locale);
		try {
			final Execution exec = Executions.getCurrent();
			sb.append(loadJS(exec, "~./js/zk/lang/mesg*.js"));
			sb.append(Wpds.outLocaleJavaScript());
			sb.append(loadJS(exec, "~./js/zul/lang/msgzul*.js"));
		} finally {
			Locales.setThreadLocal(oldl);
		}
		response(new AuScript(null, sb.toString()));
	}
	private static String loadJS(Execution exec, String path)
	throws IOException {
		path = exec.locate(path);
		InputStream is = exec.getDesktop().getWebApp().getResourceAsStream(path);
		if (is == null)
			throw new UiException("Unable to load "+path);
		final byte[] bs = Files.readAll(is);
		Files.close(is);
		return new String(bs, "UTF-8"); //UTF-8 is assumed
	}
}
