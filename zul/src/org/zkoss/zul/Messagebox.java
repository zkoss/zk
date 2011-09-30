/* Messagebox.java

	Purpose:
		
	Description:
		
	History:
		Mon Jul 18 19:07:13     2005, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.zkoss.mesg.Messages;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.SerializableEventListener;

import org.zkoss.zul.impl.MessageboxDlg;

/**
 * Represents the message box.
 *
 * <p>You don't create {@link Messagebox} directly. Rather, use {@link #show}.
 *
 * <p>A non-XUL extension.
 *
 * @author tomyeh
 */
public class Messagebox {
	private static final Log log = Log.lookup(Messagebox.class);
	private static String _templ = "~./zul/html/messagebox.zul";

	/** A symbol consisting of a question mark in a circle.
	 * <p>Since 3.5.0, they are actually style class names to display the icon.
	 */
	public static final String QUESTION = "z-msgbox z-msgbox-question";
	/** A symbol consisting of an exclamation point in a triangle with
	 * a yellow background
	 * <p>Since 3.5.0, they are actually style class names to display the icon.
	 */
	public static final String EXCLAMATION  = "z-msgbox z-msgbox-exclamation";
	/** A symbol of a lowercase letter i in a circle.
	 * <p>Since 3.5.0, they are actually style class names to display the icon.
	 */
	public static final String INFORMATION = "z-msgbox z-msgbox-information";
	/** A symbol consisting of a white X in a circle with a red background.
	 * <p>Since 3.5.0, they are actually style class names to display the icon.
	 */
	public static final String ERROR = "z-msgbox z-msgbox-error";		
	/** Contains no symbols. */
	public static final String NONE = null;

	/** A OK button. */
	public static final int OK = 0x0001;
	/** A Cancel button. */
	public static final int CANCEL = 0x0002;
	/** A Yes button. */
	public static final int YES = 0x0010;
	/** A No button. */
	public static final int NO = 0x0020;
	/** A Abort button. */
	public static final int ABORT = 0x0100;
	/** A Retry button. */
	public static final int RETRY = 0x0200;
	/** A IGNORE button. */
	public static final int IGNORE = 0x0400;

	/** The event to indicate the Yes button being clicked.
	 * @since 5.0.8
	 */
	public static final String ON_YES = "onYes";
	/** The event to indicate the No button being clicked.
	 * @since 5.0.8
	 */
	public static final String ON_NO = "onNo";
	/** The event to indicate the RETRY button being clicked.
	 * @since 5.0.8
	 */
	public static final String ON_RETRY = "onRetry";
	/** The event to indicate the Abort button being clicked.
	 * @since 5.0.8
	 */
	public static final String ON_ABORT = "onAbort";
	/** The event to indicate the Ignore button being clicked.
	 * @since 5.0.8
	 */
	public static final String ON_IGNORE = "onIgnore";
	/** The event to indicate the OK button being clicked.
	 * @since 5.0.8
	 */
	public static final String ON_OK = Events.ON_OK;
	/** The event to indicate the Cancel button being clicked.
	 * @since 5.0.8
	 */
	public static final String ON_CANCEL = Events.ON_CANCEL;

	/** Shows a message box and returns what button is pressed.
	 *
	 * @param title the title. If null, {@link WebApp#getAppName} is used.
	 * @param buttons an array of buttons to show.
	 * The buttons will be displayed in the same order in the array.
	 * @param icon one of predefined images: {@link #QUESTION},
	 * {@link #EXCLAMATION}, {@link #ERROR}, {@link #NONE}, or any style class
	 * name(s) to show an image.
	 * @param focus one of button to have to focus. If null, the first button
	 * will gain the focus.
	 * @param listener the event listener which is invoked when a button
	 * is clicked. Ignored if null.
	 * It is useful if the event processing thread is disabled
	 * ({@link org.zkoss.zk.ui.util.Configuration#enableEventThread}).
	 * If the event processing thread is disabled (system default), this method always
	 * return {@link Button#OK}. To know which button is pressed, you have to pass an
	 * event listener. Then, when the user clicks a button, the event
	 * listener is invoked with an instance of {@link ClickEvent}.
	 * You can identify which button is clicked
	 * by examining {@link ClickEvent#getButton} or {@link ClickEvent#getName}.
	 * If the close button is clicked, the onClose event is sent and
	 * {@link ClickEvent#getButton} return null;
	 * @return the button being pressed.
	 * Note: if the event processing thread is disabled (system default), it always
	 * returns {@link Button#OK}.
	 * @since 6.0.0
	 */
	public static Button show(String message, String title, Button[] buttons, String icon,
	Button focus, EventListener<ClickEvent> listener) {
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("message", message);
		params.put("title", title != null ? title:
			Executions.getCurrent().getDesktop().getWebApp().getAppName());
		params.put("icon", icon);

		final MessageboxDlg dlg = (MessageboxDlg)
			Executions.createComponents(_templ, null, params);
		dlg.setEventListener(listener);
		dlg.setButtons(buttons);
		if (focus != null) dlg.setFocus(focus);

		if (dlg.getDesktop().getWebApp().getConfiguration().isEventThreadEnabled()) {
			try {
				dlg.doModal();
			} catch (Throwable ex) {
				try {
					dlg.detach();
				} catch (Throwable ex2) {
					log.warningBriefly("Failed to detach when recovering from an error", ex2);
				}
				throw UiException.Aide.wrap(ex);
			}
			return dlg.getResult();
		} else {
			dlg.doHighlighted();
			return Button.OK;
		}
	}
	/** Shows a message box and returns what button is pressed.
	 * A shortcut to show(message, title, buttons, icon, null, listener).
	 * @since 6.0.0
	 */
	public static Button show(String message, String title, Button[] buttons, String icon,
	EventListener<ClickEvent> listener) {
		return show(message, title, buttons, icon, null, listener);
	}
	/** Shows a message box and returns what button is pressed.
	 * A shortcut to show(message, null, buttons, INFORMATION, null, listener).
	 * @since 6.0.0
	 */
	public static Button show(String message, Button[] buttons, EventListener<ClickEvent> listener) {
		return show(message, null, buttons, INFORMATION, null, listener);
	}

	/** Shows a message box and returns what button is pressed.
	 *
	 * @param title the title. If null, {@link WebApp#getAppName} is used.
	 * @param buttons a combination of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}. If zero, {@link #OK} is assumed
	 * @param icon one of predefined images: {@link #QUESTION},
	 * {@link #EXCLAMATION}, {@link #ERROR}, {@link #NONE}, or any style class
	 * name(s) to show an image.
	 * @return the button being pressed (one of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}).
	 * Note: if the event processing thread is disabled, it always
	 * returns {@link #OK}.
	 */
	public static 
	int show(String message, String title, int buttons, String icon) {
		return show(message, title, buttons, icon, 0, null);
	}
	/** Shows a message box and returns what button is pressed.
	 *
	 * @param title the title. If null, {@link WebApp#getAppName} is used.
	 * @param buttons a combination of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}. If zero, {@link #OK} is assumed
	 * @param icon one of predefined images: {@link #QUESTION},
	 * {@link #EXCLAMATION}, {@link #ERROR}, {@link #NONE}, or any style class
	 * name(s) to show an image.
	 * @param listener the event listener which is invoked when a button
	 * is clicked. Ignored if null.
	 * It is useful if the event processing thread is disabled
	 * ({@link org.zkoss.zk.ui.util.Configuration#enableEventThread}).
	 * If the event processing thread is disabled, this method always
	 * return {@link #OK}. To know which button is pressed, you have to pass an
	 * event listener. Then, when the user clicks a button, the event
	 * listener is invoked. You can identify which button is clicked
	 * by examining the event name ({@link org.zkoss.zk.ui.event.Event#getName}) as shown
	 * in the following table. Alternatively, you can examine the value
	 * of {@link org.zkoss.zk.ui.event.Event#getData}, which must be an
	 * integer represetinng the button, such as {@link #OK}, {@link #YES}
	 * and so on.
	 * <table border="1">
	 *<tr><td>Button Name</td><td>Event Name</td></tr>
	 *<tr><td>OK</td><td>onOK ({@link #ON_OK})</td></tr>
	 *<tr><td>Cancel</td><td>onCancel ({@link #ON_CANCEL})</td></tr>
	 *<tr><td>Yes</td><td>onYes ({@link #ON_YES})</td></tr>
	 *<tr><td>No</td><td>onNo ({@link #ON_NO})</td></tr>
	 *<tr><td>Retry</td><td>onRetry ({@link #ON_RETRY})</td></tr>
	 *<tr><td>Abort</td><td>onAbort ({@link #ON_ABORT})</td></tr>
	 *<tr><td>Ignore</td><td>onIgnore ({@link #ON_IGNORE})</td></tr>
	 *</table>
	 * @return the button being pressed (one of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}).
	 * Note: if the event processing thread is disabled, it always
	 * returns {@link #OK}.
	 * @since 3.0.4
	 */
	public static int show(String message, String title, int buttons, String icon,
	EventListener<Event> listener) {
		return show(message, title, buttons, icon, 0, listener);
	}
	/** Shows a message box and returns what button is pressed.
	 *
	 * @param title the title. If null, {@link WebApp#getAppName} is used.
	 * @param buttons a combination of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}. If zero, {@link #OK} is assumed
	 * @param icon one of predefined images: {@link #QUESTION},
	 * {@link #EXCLAMATION}, {@link #ERROR}, {@link #NONE}, or any style class
	 * name(s) to show an image.
	 * @param focus one of button to have to focus. If 0, the first button
	 * will gain the focus. One of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}.
	 * @return the button being pressed (one of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}).
	 * Note: if the event processing thread is disabled, it always
	 * returns {@link #OK}.
	 * @since 3.0.0
	 */
	public static int show(String message, String title, int buttons, String icon, int focus) {
		return show(message, title, buttons, icon, focus, null);
	}
	/** Shows a message box and returns what button is pressed.
	 *
	 * @param title the title. If null, {@link WebApp#getAppName} is used.
	 * @param buttons a combination of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}. If zero, {@link #OK} is assumed
	 * @param icon one of predefined images: {@link #QUESTION},
	 * {@link #EXCLAMATION}, {@link #ERROR}, {@link #NONE}, or any style class
	 * name(s) to show an image.
	 * @param focus one of button to have to focus. If 0, the first button
	 * will gain the focus. One of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}.
	 * @param listener the event listener which is invoked when a button
	 * is clicked. Ignored if null.
	 * It is useful if the event processing thread is disabled
	 * ({@link org.zkoss.zk.ui.util.Configuration#enableEventThread}).
	 * If the event processing thread is disabled, this method always
	 * return {@link #OK}. To know which button is pressed, you have to pass an
	 * event listener. Then, when the user clicks a button, the event
	 * listener is invoked. You can identify which button is clicked
	 * by examining the event name ({@link org.zkoss.zk.ui.event.Event#getName}) as shown
	 * in the following table. Alternatively, you can examine the value
	 * of {@link org.zkoss.zk.ui.event.Event#getData}, which must be an
	 * integer represetinng the button, such as {@link #OK}, {@link #YES}
	 * and so on.
	 * <table border="1">
	 *<tr><td>Button</td><td>Event Name</td></tr>
	 *<tr><td>OK</td><td>onOK ({@link #ON_OK})</td></tr>
	 *<tr><td>Cancel</td><td>onCancel ({@link #ON_CANCEL})</td></tr>
	 *<tr><td>Yes</td><td>onYes ({@link #ON_YES})</td></tr>
	 *<tr><td>No</td><td>onNo ({@link #ON_NO})</td></tr>
	 *<tr><td>Retry</td><td>onRetry ({@link #ON_RETRY})</td></tr>
	 *<tr><td>Abort</td><td>onAbort ({@link #ON_ABORT})</td></tr>
	 *<tr><td>Ignore</td><td>onIgnore ({@link #ON_IGNORE})</td></tr>
	 *</table>
	 * @return the button being pressed (one of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}).
	 * Note: if the event processing thread is disabled, it always
	 * returns {@link #OK}.
	 * @since 3.0.4
	 */
	public static int show(String message, String title, int buttons, String icon,
	int focus, EventListener<Event> listener) {
		return show(message, title, toButtonTypes(buttons), icon,
			focus != 0 ? toButtonType(focus): null,
			toButtonListener(listener)).value;
	}
	private static Button toButtonType(int btn) {
		switch (btn) {
		case CANCEL: return Button.CANCEL;
		case YES: return Button.YES;
		case NO: return Button.NO;
		case ABORT: return Button.ABORT;
		case RETRY: return Button.RETRY;
		case IGNORE: return Button.IGNORE;
		default: return Button.OK;
		}
	}
	private static Button[] toButtonTypes(int buttons) {
		final List<Button> btntypes = new ArrayList<Button>();
		if ((buttons & OK) != 0)
			btntypes.add(toButtonType(OK));
		if ((buttons & CANCEL) != 0)
			btntypes.add(toButtonType(CANCEL));
		if ((buttons & YES) != 0)
			btntypes.add(toButtonType(YES));
		if ((buttons & NO) != 0)
			btntypes.add(toButtonType(NO));
		if ((buttons & RETRY) != 0)
			btntypes.add(toButtonType(RETRY));
		if ((buttons & ABORT) != 0)
			btntypes.add(toButtonType(ABORT));
		if ((buttons & IGNORE) != 0)
			btntypes.add(toButtonType(IGNORE));
		return btntypes.toArray(new Button[btntypes.size()]);
	}
	private static
	EventListener<ClickEvent> toButtonListener(EventListener<Event> listener) {
		return listener != null ? new ButtonListener(listener): null;
	}

	/** Shows a message box and returns what button is pressed.
	 * A shortcut to show(message, null, OK, INFORMATION).
	 */
	public static int show(String message) {
		return show(message, null, OK, INFORMATION, 0, null);
	}
	/** Shows a message box by specifying a message code, and returns what
	 * button is pressed.
	 *
	 * @param titleCode the message code for the title. If non-positive,
	 * the default title is used.
	 * @return the button being pressed (one of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}).
	 * Note: if the event processing thread is disabled, it always
	 * returns {@link #OK}.
	 */
	public static int show(int messageCode, Object[] args, int titleCode, int buttons,
	String icon) {
		return show(messageCode, args, titleCode, buttons, icon, 0, null);
	}
	/** Shows a message box by specifying a message code, and returns what
	 * button is pressed.
	 *
	 * @param titleCode the message code for the title. If non-positive,
	 * the default title is used.
	 * @param focus one of button to have to focus. If 0, the first button
	 * will gain the focus. One of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}.
	 * @return the button being pressed (one of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}).
	 * Note: if the event processing thread is disabled, it always
	 * returns {@link #OK}.
	 * @since 3.0.0
	 */
	public static int show(int messageCode, Object[] args, int titleCode, int buttons,
	String icon, int focus) {
		return show(messageCode, args, titleCode, buttons, icon, focus, null);
	}
	/** Shows a message box by specifying a message code, and returns what
	 * button is pressed.
	 *
	 * @param titleCode the message code for the title. If non-positive,
	 * the default title is used.
	 * @param focus one of button to have to focus. If 0, the first button
	 * will gain the focus. One of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}.
	 * @param listener the event listener which is invoked when a button
	 * is clicked. Ignored if null.
	 * It is useful if the event processing thread is disabled
	 * ({@link org.zkoss.zk.ui.util.Configuration#enableEventThread}).
	 * If the event processing thread is disabled, this method always
	 * return {@link #OK}. To know which button is pressed, you have to pass an
	 * event listener. Then, when the user clicks a button, the event
	 * listener is invoked. You can identify which button is clicked
	 * by examining the event name ({@link org.zkoss.zk.ui.event.Event#getName}) as shown
	 * in the following table. Alternatively, you can examine the value
	 * of {@link org.zkoss.zk.ui.event.Event#getData}, which must be an
	 * integer represetinng the button, such as {@link #OK}, {@link #YES}
	 * and so on.
	 * <table border="1">
	 *<tr><td>Button</td><td>Event Name</td></tr>
	 *<tr><td>OK</td><td>onOK ({@link #ON_OK})</td></tr>
	 *<tr><td>Cancel</td><td>onCancel ({@link #ON_CANCEL})</td></tr>
	 *<tr><td>Yes</td><td>onYes ({@link #ON_YES})</td></tr>
	 *<tr><td>No</td><td>onNo ({@link #ON_NO})</td></tr>
	 *<tr><td>Retry</td><td>onRetry ({@link #ON_RETRY})</td></tr>
	 *<tr><td>Abort</td><td>onAbort ({@link #ON_ABORT})</td></tr>
	 *<tr><td>Ignore</td><td>onIgnore ({@link #ON_IGNORE})</td></tr>
	 *</table>
	 * @return the button being pressed (one of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}).
	 * Note: if the event processing thread is disabled, it always
	 * returns {@link #OK}.
	 * @since 3.0.4
	 */
	public static int show(int messageCode, Object[] args, int titleCode, int buttons,
	String icon, int focus, EventListener<Event> listener) {
		return show(Messages.get(messageCode, args),
			titleCode > 0 ? Messages.get(titleCode): null, buttons,
			icon, focus, listener);
	}
	/** Shows a message box by specifying a message code, and returns what
	 * button is pressed.
	 *
	 * @param titleCode the message code for the title. If non-positive,
	 * the default title is used.
	 * @return the button being pressed (one of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}).
	 * Note: if the event processing thread is disabled, it always
	 * returns {@link #OK}.
	 */
	public static int show(int messageCode, Object arg, int titleCode, int buttons, String icon) {
		return show(messageCode, arg, titleCode, buttons, icon, 0, null);
	}
	/** Shows a message box by specifying a message code, and returns what
	 * button is pressed.
	 *
	 * @param titleCode the message code for the title. If non-positive,
	 * the default title is used.
	 * @param focus one of button to have to focus. If 0, the first button
	 * will gain the focus. One of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}.
	 * @return the button being pressed (one of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}).
	 * Note: if the event processing thread is disabled, it always
	 * returns {@link #OK}.
	 * @since 3.0.0
	 */
	public static int show(int messageCode, Object arg, int titleCode, int buttons,
	String icon, int focus) {
		return show(messageCode, arg, titleCode, buttons, icon, focus, null);
	}
	/** Shows a message box by specifying a message code, and returns what
	 * button is pressed.
	 *
	 * @param titleCode the message code for the title. If non-positive,
	 * the default title is used.
	 * @param focus one of button to have to focus. If 0, the first button
	 * will gain the focus. One of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}.
	 * @param listener the event listener which is invoked when a button
	 * is clicked. Ignored if null.
	 * It is useful if the event processing thread is disabled
	 * ({@link org.zkoss.zk.ui.util.Configuration#enableEventThread}).
	 * If the event processing thread is disabled, this method always
	 * return {@link #OK}. To know which button is pressed, you have to pass an
	 * event listener. Then, when the user clicks a button, the event
	 * listener is invoked. You can identify which button is clicked
	 * by examining the event name ({@link org.zkoss.zk.ui.event.Event#getName}) as shown
	 * in the following table. Alternatively, you can examine the value
	 * of {@link org.zkoss.zk.ui.event.Event#getData}, which must be an
	 * integer represetinng the button, such as {@link #OK}, {@link #YES}
	 * and so on.
	 * <table border="1">
	 *<tr><td>Button</td><td>Event Name</td></tr>
	 *<tr><td>OK</td><td>onOK ({@link #ON_OK})</td></tr>
	 *<tr><td>Cancel</td><td>onCancel ({@link #ON_CANCEL})</td></tr>
	 *<tr><td>Yes</td><td>onYes ({@link #ON_YES})</td></tr>
	 *<tr><td>No</td><td>onNo ({@link #ON_NO})</td></tr>
	 *<tr><td>Retry</td><td>onRetry ({@link #ON_RETRY})</td></tr>
	 *<tr><td>Abort</td><td>onAbort ({@link #ON_ABORT})</td></tr>
	 *<tr><td>Ignore</td><td>onIgnore ({@link #ON_IGNORE})</td></tr>
	 *</table>
	 * @return the button being pressed (one of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}).
	 * Note: if the event processing thread is disabled, it always
	 * returns {@link #OK}.
	 * @since 3.0.4
	 */
	public static int show(int messageCode, Object arg, int titleCode, int buttons,
	String icon, int focus, EventListener<Event> listener) {
		return show(Messages.get(messageCode, arg),
			titleCode > 0 ? Messages.get(titleCode): null, buttons,
			icon, focus, listener);
	}
	/** Shows a message box by specifying a message code, and returns what
	 * button is pressed.
	 *
	 * @param titleCode the message code for the title. If non-positive,
	 * the default title is used.
	 */
	public static int show(int messageCode, int titleCode, int buttons, String icon) {
		return show(messageCode, titleCode, buttons, icon, 0);
	}
	/** Shows a message box by specifying a message code, and returns what
	 * button is pressed.
	 *
	 * @param titleCode the message code for the title. If non-positive,
	 * the default title is used.
	 * @param focus one of button to have to focus. If 0, the first button
	 * will gain the focus. One of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}.
	 * @since 3.0.0
	 */
	public static int show(int messageCode, int titleCode, int buttons, String icon,
	int focus) {
		return show(messageCode, titleCode, buttons, icon, focus, null);
	}
	/** Shows a message box by specifying a message code, and returns what
	 * button is pressed.
	 *
	 * @param titleCode the message code for the title. If non-positive,
	 * the default title is used.
	 * @param focus one of button to have to focus. If 0, the first button
	 * will gain the focus. One of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}.
	 * @param listener the event listener which is invoked when a button
	 * is clicked. Ignored if null.
	 * It is useful if the event processing thread is disabled
	 * ({@link org.zkoss.zk.ui.util.Configuration#enableEventThread}).
	 * If the event processing thread is disabled, this method always
	 * return {@link #OK}. To know which button is pressed, you have to pass an
	 * event listener. Then, when the user clicks a button, the event
	 * listener is invoked. You can identify which button is clicked
	 * by examining the event name ({@link org.zkoss.zk.ui.event.Event#getName}) as shown
	 * in the following table. Alternatively, you can examine the value
	 * of {@link org.zkoss.zk.ui.event.Event#getData}, which must be an
	 * integer represetinng the button, such as {@link #OK}, {@link #YES}
	 * and so on.
	 * <table border="1">
	 *<tr><td>Button</td><td>Event Name</td></tr>
	 *<tr><td>OK</td><td>onOK ({@link #ON_OK})</td></tr>
	 *<tr><td>Cancel</td><td>onCancel ({@link #ON_CANCEL})</td></tr>
	 *<tr><td>Yes</td><td>onYes ({@link #ON_YES})</td></tr>
	 *<tr><td>No</td><td>onNo ({@link #ON_NO})</td></tr>
	 *<tr><td>Retry</td><td>onRetry ({@link #ON_RETRY})</td></tr>
	 *<tr><td>Abort</td><td>onAbort ({@link #ON_ABORT})</td></tr>
	 *<tr><td>Ignore</td><td>onIgnore ({@link #ON_IGNORE})</td></tr>
	 *<tr><td>The close button on the right-top corner (x)<br>since 5.0.2</td><td>onClose</td></tr>
	 *</table>
	 * @return the button being pressed (one of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}).
	 * Note: if the event processing thread is disabled, it always
	 * returns {@link #OK}.
	 * @since 3.0.4
	 */
	public static int show(int messageCode, int titleCode, int buttons, String icon,
	int focus, EventListener<Event> listener) {
		return show(Messages.get(messageCode),
			titleCode > 0 ? Messages.get(titleCode): null, buttons,
			icon, focus, listener);
	}

	/** Sets the template used to create the message dialog.
	 *
	 * <p>The template must follow the default template:
	 * ~./zul/html/messagebox.zul
	 *
	 * <p>In other words, just adjust the label and layout and don't
	 * change the component's ID.
	 */
	public static void setTemplate(String uri) {
		if (uri == null || uri.length() == 0)
			throw new IllegalArgumentException("empty");
		_templ = uri;
	}
	/** Returns the template used to create the message dialog.
	 */
	public static String getTemplate() {
		return _templ;
	}

	/** The button types.
	 * @since 6.0.0
	 */
	public static enum Button {
		/** A OK button. */
		OK (Messagebox.OK),
		/** A Cancel button. */
		CANCEL (Messagebox.CANCEL),
		/** A Yes button. */
		YES (Messagebox.YES),
		/** A No button. */
		NO (Messagebox.NO),
		/** A Abort button. */
		ABORT (Messagebox.ABORT),
		/** A Retry button. */
		RETRY (Messagebox.RETRY),
		/** A IGNORE button. */
		IGNORE (Messagebox.IGNORE);

		/** A unique value to represent each button type. */
		public final int value;
		private Button(int v) {
			this.value = v;
		}
	}
	/** The event that will be received by the listener when the user clicks a button.
	 * @since 6.0.0
	 */
	public static class ClickEvent extends Event {
		public ClickEvent(String name, Component target, Button button) {
			super(name, target, button);
		}
		/** Returns the button being clicked. If the close button on the
		 * title is clicked, this method returns null (and {@link #getName} returns
		 * onClose).
		 */
		public Button getButton() {
			return (Button)getData();
		}
	}

	private static class ButtonListener implements SerializableEventListener<ClickEvent> {
		private final EventListener<Event> _listener;
		private ButtonListener(EventListener<Event> listener) {
			_listener = listener;
		}
		public void onEvent(ClickEvent event) throws Exception {
			final Button btn = event.getButton();
			_listener.onEvent(
				new Event(event.getName(), event.getTarget(),
					btn != null ? btn.value: -1));
		}
		public String toString() {
			return _listener.toString();
		}
	}
}
