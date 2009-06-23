/* Messagebox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jul 18 19:07:13     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Map;
import java.util.HashMap;

import org.zkoss.mesg.Messages;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.EventListener;

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
	public static final String INFORMATION = "z-msgbox z-msgbox-imformation";
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
	 * Note: if the event processing thread is disable, it always
	 * returns {@link #OK}.
	 */
	public static final
	int show(String message, String title, int buttons, String icon)
	throws InterruptedException {
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
	 * If the event processing thread is disable, this method always
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
	 *<tr><td>OK</td><td>onOK</td></tr>
	 *<tr><td>Cancel</td><td>onCancel</td></tr>
	 *<tr><td>Yes</td><td>onYes</td></tr>
	 *<tr><td>No</td><td>onNo</td></tr>
	 *<tr><td>Retry</td><td>onRetry</td></tr>
	 *<tr><td>Abort</td><td>onAbort</td></tr>
	 *<tr><td>Ignore</td><td>onIgnore</td></tr>
	 *</table>
	 * @return the button being pressed (one of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}).
	 * Note: if the event processing thread is disable, it always
	 * returns {@link #OK}.
	 * @since 3.0.4
	 */
	public static final
	int show(String message, String title, int buttons, String icon,
	EventListener listener)
	throws InterruptedException {
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
	 * Note: if the event processing thread is disable, it always
	 * returns {@link #OK}.
	 * @since 3.0.0
	 */
	public static final
	int show(String message, String title, int buttons, String icon, int focus)
	throws InterruptedException {
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
	 * If the event processing thread is disable, this method always
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
	 *<tr><td>OK</td><td>onOK</td></tr>
	 *<tr><td>Cancel</td><td>onCancel</td></tr>
	 *<tr><td>Yes</td><td>onYes</td></tr>
	 *<tr><td>No</td><td>onNo</td></tr>
	 *<tr><td>Retry</td><td>onRetry</td></tr>
	 *<tr><td>Abort</td><td>onAbort</td></tr>
	 *<tr><td>Ignore</td><td>onIgnore</td></tr>
	 *</table>
	 * @return the button being pressed (one of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}).
	 * Note: if the event processing thread is disable, it always
	 * returns {@link #OK}.
	 * @since 3.0.4
	 */
	public static final
	int show(String message, String title, int buttons, String icon,
	int focus, EventListener listener)
	throws InterruptedException {
		final Map params = new HashMap();
		params.put("message", message);
		params.put("title", title != null ? title:
			Executions.getCurrent().getDesktop().getWebApp().getAppName());
		params.put("icon", icon);
		params.put("buttons", new Integer(
			(buttons & (OK|CANCEL|YES|NO|ABORT|RETRY|IGNORE)) != 0 ? buttons: OK));
		if ((buttons & OK) != 0)
			params.put("OK", new Integer(OK));
		if ((buttons & CANCEL) != 0)
			params.put("CANCEL", new Integer(CANCEL));
		if ((buttons & YES) != 0)
			params.put("YES", new Integer(YES));
		if ((buttons & NO) != 0)
			params.put("NO", new Integer(NO));
		if ((buttons & RETRY) != 0)
			params.put("RETRY", new Integer(RETRY));
		if ((buttons & ABORT) != 0)
			params.put("ABORT", new Integer(ABORT));
		if ((buttons & IGNORE) != 0)
			params.put("IGNORE", new Integer(IGNORE));

		final MessageboxDlg dlg = (MessageboxDlg)
			Executions.createComponents(_templ, null, params);
		dlg.setButtons(buttons);
		dlg.setEventListener(listener);
		if (focus > 0) dlg.setFocus(focus);

		if (dlg.getDesktop().getWebApp().getConfiguration().isEventThreadEnabled()) {
			try {
				dlg.doModal();
			} catch (Throwable ex) {
				if (ex instanceof InterruptedException)
					throw (InterruptedException)ex;
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
			return OK;
		}
	}
	/** Shows a message box and returns what button is pressed.
	 * A shortcut to show(message, null, OK, INFORMATION).
	 */
	public static final int show(String message)
	throws InterruptedException {
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
	 * Note: if the event processing thread is disable, it always
	 * returns {@link #OK}.
	 */
	public static final
	int show(int messageCode, Object[] args, int titleCode, int buttons,
	String icon)
	throws InterruptedException {
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
	 * Note: if the event processing thread is disable, it always
	 * returns {@link #OK}.
	 * @since 3.0.0
	 */
	public static final
	int show(int messageCode, Object[] args, int titleCode, int buttons,
	String icon, int focus)
	throws InterruptedException {
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
	 * If the event processing thread is disable, this method always
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
	 *<tr><td>OK</td><td>onOK</td></tr>
	 *<tr><td>Cancel</td><td>onCancel</td></tr>
	 *<tr><td>Yes</td><td>onYes</td></tr>
	 *<tr><td>No</td><td>onNo</td></tr>
	 *<tr><td>Retry</td><td>onRetry</td></tr>
	 *<tr><td>Abort</td><td>onAbort</td></tr>
	 *<tr><td>Ignore</td><td>onIgnore</td></tr>
	 *</table>
	 * @return the button being pressed (one of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}).
	 * Note: if the event processing thread is disable, it always
	 * returns {@link #OK}.
	 * @since 3.0.4
	 */
	public static final
	int show(int messageCode, Object[] args, int titleCode, int buttons,
	String icon, int focus, EventListener listener)
	throws InterruptedException {
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
	 * Note: if the event processing thread is disable, it always
	 * returns {@link #OK}.
	 */
	public static final
	int show(int messageCode, Object arg, int titleCode, int buttons, String icon)
	throws InterruptedException {
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
	 * Note: if the event processing thread is disable, it always
	 * returns {@link #OK}.
	 * @since 3.0.0
	 */
	public static final
	int show(int messageCode, Object arg, int titleCode, int buttons,
	String icon, int focus)
	throws InterruptedException {
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
	 * If the event processing thread is disable, this method always
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
	 *<tr><td>OK</td><td>onOK</td></tr>
	 *<tr><td>Cancel</td><td>onCancel</td></tr>
	 *<tr><td>Yes</td><td>onYes</td></tr>
	 *<tr><td>No</td><td>onNo</td></tr>
	 *<tr><td>Retry</td><td>onRetry</td></tr>
	 *<tr><td>Abort</td><td>onAbort</td></tr>
	 *<tr><td>Ignore</td><td>onIgnore</td></tr>
	 *</table>
	 * @return the button being pressed (one of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}).
	 * Note: if the event processing thread is disable, it always
	 * returns {@link #OK}.
	 * @since 3.0.4
	 */
	public static final
	int show(int messageCode, Object arg, int titleCode, int buttons,
	String icon, int focus, EventListener listener)
	throws InterruptedException {
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
	public static final
	int show(int messageCode, int titleCode, int buttons, String icon)
	throws InterruptedException {
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
	public static final
	int show(int messageCode, int titleCode, int buttons, String icon,
	int focus)
	throws InterruptedException {
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
	 * If the event processing thread is disable, this method always
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
	 *<tr><td>OK</td><td>onOK</td></tr>
	 *<tr><td>Cancel</td><td>onCancel</td></tr>
	 *<tr><td>Yes</td><td>onYes</td></tr>
	 *<tr><td>No</td><td>onNo</td></tr>
	 *<tr><td>Retry</td><td>onRetry</td></tr>
	 *<tr><td>Abort</td><td>onAbort</td></tr>
	 *<tr><td>Ignore</td><td>onIgnore</td></tr>
	 *</table>
	 * @return the button being pressed (one of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}).
	 * Note: if the event processing thread is disable, it always
	 * returns {@link #OK}.
	 * @since 3.0.4
	 */
	public static final
	int show(int messageCode, int titleCode, int buttons, String icon,
	int focus, EventListener listener)
	throws InterruptedException {
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
}
