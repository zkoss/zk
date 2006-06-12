/* Messagebox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Nov 24 15:09:25     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zhtml;

import com.potix.mesg.Messages;
import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.UiException;

/**
 * Represents the message box.
 *
 * <p>You don't create {@link Messagebox} directly. Rather, use {@link #show}.
 * 
 * <p>A non-XHTML extension.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Messagebox {
	/** A symbol consisting of a question mark in a circle. */
	public static final String QUESTION = com.potix.zul.html.Messagebox.QUESTION;
	/** A symbol consisting of an exclamation point in a triangle with
	 * a yellow background.
	 */
	public static final String EXCLAMATION  = com.potix.zul.html.Messagebox.EXCLAMATION;
	/** The same as {@link #EXCLAMATION}.
	 */
	public static final String INFORMATION = EXCLAMATION;
	/** A symbol consisting of a white X in a circle with a red background. */
	public static final String ERROR = com.potix.zul.html.Messagebox.ERROR;
	/** Contains no symbols. */
	public static final String NONE = null;

	/** A OK button. */
	public static final int OK = com.potix.zul.html.Messagebox.OK;
	/** A Cancel button. */
	public static final int CANCEL = com.potix.zul.html.Messagebox.CANCEL;
	/** A Yes button. */
	public static final int YES = com.potix.zul.html.Messagebox.YES;
	/** A No button. */
	public static final int NO = com.potix.zul.html.Messagebox.NO;
	/** A Abort button. */
	public static final int ABORT = com.potix.zul.html.Messagebox.ABORT;
	/** A Retry button. */
	public static final int RETRY = com.potix.zul.html.Messagebox.RETRY;
	/** A IGNORE button. */
	public static final int IGNORE = com.potix.zul.html.Messagebox.IGNORE;

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
		return com.potix.zul.html.Messagebox.show(message, title, buttons, icon);
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
