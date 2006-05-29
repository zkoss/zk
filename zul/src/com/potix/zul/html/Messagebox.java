/* Messagebox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jul 18 19:07:13     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.Map;
import java.util.HashMap;

import com.potix.mesg.Messages;
import com.potix.zul.mesg.MZul;
import com.potix.util.prefs.Apps;

import com.potix.zk.ui.Executions;

import com.potix.zul.html.impl.MessageboxDlg;

/**
 * Represents the message box.
 *
 * <p>You don't create {@link Messagebox} directly. Rather, use {@link #show}.
 *
 * <p>A non-XUL extension.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Messagebox {
	/** A symbol consisting of a question mark in a circle. */
	public static final String QUESTION = "~./zul/img/question.gif";
	/** A symbol consisting of an exclamation point in a triangle with
	 * a yellow background.
	 */
	public static final String EXCLAMATION  = "~./zul/img/exclamation.gif";
	/** The same as {@link #EXCLAMATION}.
	 */
	public static final String INFORMATION = EXCLAMATION;
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
	 * @param title the title. If null, {@link com.potix.util.prefs.App#getName} is used.
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
		params.put("title", title != null ? title: Apps.getApp().getName());
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
			Executions.createComponents(
			"~./zul/html/messagebox.zul", null, params);
		dlg.setButtons(buttons);
		dlg.doModal();
		return dlg.getResult();
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
}
