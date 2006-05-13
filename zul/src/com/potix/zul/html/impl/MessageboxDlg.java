/* MessageboxDlg.java

{{IS_NOTE
	$Id: MessageboxDlg.java,v 1.5 2006/02/27 03:55:17 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Wed Aug 17 16:42:20     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html.impl;

import com.potix.zul.html.Window;
import com.potix.zk.ui.UiException;

import com.potix.zul.html.Messagebox;

/**
 * Used with {@link Messagebox} to implement a message box.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.5 $ $Date: 2006/02/27 03:55:17 $
 */
public class MessageboxDlg extends Window {
	/** A OK button. */
	public static final int OK = Messagebox.OK;
	/** A Cancel button. */
	public static final int CANCEL = Messagebox.CANCEL;
	/** A Yes button. */
	public static final int YES = Messagebox.YES;
	/** A No button. */
	public static final int NO = Messagebox.NO;
	/** A Abort button. */
	public static final int ABORT = Messagebox.ABORT;
	/** A Retry button. */
	public static final int RETRY = Messagebox.RETRY;
	/** A IGNORE button. */
	public static final int IGNORE = Messagebox.IGNORE;

	/** What buttons are allowed. */
	private int _buttons;
	/** Which button is pressed. */
	private int _result;

	public void onOK() {
		if ((_buttons & OK) != 0) endModal(OK);
		else if ((_buttons & YES) != 0) endModal(YES);
		else if ((_buttons & RETRY) != 0) endModal(RETRY);
	}
	public void onCancel() {
		if (_buttons == OK) endModal(OK);
		else if ((_buttons & CANCEL) != 0) endModal(CANCEL);
		else if ((_buttons & NO) != 0) endModal(NO);
		else if ((_buttons & ABORT) != 0) endModal(ABORT);
	}

	/** Sets what buttons are allowed. */
	public void setButtons(int buttons) {
		_buttons = buttons;
	}

	/** Called only internally.
	 */
	public void endModal(int button) {
		_result = button;
		detach();
	}
	/** Returns the result which is the button being pressed.
	 */
	public int getResult() {
		return _result;
	}
}
