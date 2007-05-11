/* Messagebox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jul 18 19:07:13     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Map;
import java.util.HashMap;

import org.zkoss.mesg.Messages;
import org.zkoss.zul.mesg.MZul;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;

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
	private static String _templ = "~./zul/html/messagebox.zul";

	/** A symbol consisting of a question mark in a circle. */
	public static final String QUESTION = "~./zul/img/question.gif";
	/** A symbol consisting of an exclamation point in a triangle with
	 * a yellow background.
	 */
	public static final String EXCLAMATION  = "~./zul/img/exclamation.gif";
	/** A symbol of a lowercase letter i in a circle.
	 */
	public static final String INFORMATION = "~./zul/img/information.gif";
	/** A symbol consisting of a white X in a circle with a red background. */
	public static final String ERROR = "~./zul/img/error.gif";
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
	 * {@link #EXCLAMATION}, {@link #ERROR}, {@link #NONE}, or any URI of
	 * an image.
	 * @return the button being pressed (one of {@link #OK}, {@link #CANCEL},
	 * {@link #YES}, {@link #NO}, {@link #ABORT}, {@link #RETRY},
	 * and {@link #IGNORE}).
	 */
	public static final
	int show(String message, String title, int buttons, String icon)
	throws InterruptedException {
		final Map params = new HashMap();
		params.put("message", message);
		params.put("title", title != null ? title:
			Executions.getCurrent().getDesktop().getWebApp().getAppName());
		params.put("icon", icon);
		params.put("buttons", new Integer(
			(buttons & (OK|CANCEL|YES|NO|ABORT|RETRY|IGNORE)) != 0 ? buttons: OK));
		if ((buttons & OK) != 0)
			params.put("OK", Messages.get(MZul.OK));
		if ((buttons & CANCEL) != 0)
			params.put("CANCEL", Messages.get(MZul.CANCEL));
		if ((buttons & YES) != 0)
			params.put("YES", Messages.get(MZul.YES));
		if ((buttons & NO) != 0)
			params.put("NO", Messages.get(MZul.NO));
		if ((buttons & RETRY) != 0)
			params.put("RETRY", Messages.get(MZul.RETRY));
		if ((buttons & ABORT) != 0)
			params.put("ABORT", Messages.get(MZul.ABORT));
		if ((buttons & IGNORE) != 0)
			params.put("IGNORE", Messages.get(MZul.IGNORE));

		final MessageboxDlg dlg = (MessageboxDlg)
			Executions.createComponents(_templ, null, params);
		dlg.setButtons(buttons);

		if (dlg.getDesktop().getWebApp().getConfiguration().isEventThreadEnabled()) {
			try {
				dlg.doModal();
			} catch (Throwable ex) {
				dlg.detach();
				if (ex instanceof InterruptedException)
					throw (InterruptedException)ex;
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
		return show(message, null, OK, INFORMATION);
	}
	/** Shows a message box by specifying a message code, and returns what
	 * button is pressed.
	 *
	 * @param titleCode the message code for the title. If non-positive,
	 * the default title is used.
	 */
	public static final
	int show(int messageCode, Object[] args, int titleCode, int button, String icon)
	throws InterruptedException {
		return show(Messages.get(messageCode, args),
			titleCode > 0 ? Messages.get(titleCode): null, button, icon);
	}
	/** Shows a message box by specifying a message code, and returns what
	 * button is pressed.
	 *
	 * @param titleCode the message code for the title. If non-positive,
	 * the default title is used.
	 */
	public static final
	int show(int messageCode, Object arg, int titleCode, int button, String icon)
	throws InterruptedException {
		return show(Messages.get(messageCode, arg),
			titleCode > 0 ? Messages.get(titleCode): null, button, icon);
	}
	/** Shows a message box by specifying a message code, and returns what
	 * button is pressed.
	 *
	 * @param titleCode the message code for the title. If non-positive,
	 * the default title is used.
	 */
	public static final
	int show(int messageCode, int titleCode, int button, String icon)
	throws InterruptedException {
		return show(Messages.get(messageCode),
			titleCode > 0 ? Messages.get(titleCode): null, button, icon);
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
