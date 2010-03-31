/* MessageboxDlg.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 17 16:42:20     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import org.zkoss.mesg.Messages;
import org.zkoss.zul.mesg.MZul;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;

import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox;

/**
 * Used with {@link Messagebox} to implement a message box.
 *
 * @author tomyeh
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
	/** The event lisetener. */
	private EventListener _listener;

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
	/** Sets the event listener.
	 * @param listener the event listener. If null, no invocation at all.
	 * @since 3.0.4
	 */
	public void setEventListener(EventListener listener) {
		_listener = listener;
	}
	/** Sets the focus.
	 * @param button the button to gain the focus. If 0, the default one
	 * (i.e., the first one) is assumed.
	 * @since 3.0.0
	 */
	public void setFocus(int button) {
		if (button > 0) {
			final Button btn = (Button)getFellowIfAny("btn" + button);
			if (btn != null)
				btn.focus();
		}
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

	//Override//
	public void onClose() {
		if (_listener != null) {
			final Event evt = new Event(Events.ON_CLOSE, this, new Integer(-1));
			try {
				_listener.onEvent(evt);
				if (!evt.isPropagatable())
					return; //no more processing
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex);
			}
		}
		super.onClose();
	}

	/**
	 * Represents a button on the message box.
	 * @since 3.0.0
	 */
	public static class Button extends org.zkoss.zul.Button {
		private int _button;
		private String _evtnm;

		/** Sets the identity.
		 */
		public void setIdentity(int button) {
			_button = button;

			final int label;
			switch (button) {
			case YES:
				label = MZul.YES;
				_evtnm = "onYes";
				break;
			case NO:
				label = MZul.NO;
				_evtnm = "onNo";
				break;
			case RETRY:
				label = MZul.RETRY;
				_evtnm = "onRetry";
				break;
			case ABORT:
				label = MZul.ABORT;
				_evtnm = "onAbort";
				break;
			case IGNORE:
				label = MZul.IGNORE;
				_evtnm = "onIgnore";
				break;
			case CANCEL:
				label = MZul.CANCEL;
				_evtnm = "onCancel";
				break;
			default:
				label = MZul.OK;
				_evtnm = "onOK";
				break;
			}
			setLabel(Messages.get(label));
			setId("btn" + _button);
		}
		public void onClick() throws Exception {
			final MessageboxDlg dlg = (MessageboxDlg)getSpaceOwner();
			if (dlg._listener != null) {
				final Event evt = new Event(_evtnm, dlg, new Integer(_button));
				dlg._listener.onEvent(evt);
				if (!evt.isPropagatable())
					return; //no more processing
			}
			dlg.endModal(_button);
		}
	}
}
