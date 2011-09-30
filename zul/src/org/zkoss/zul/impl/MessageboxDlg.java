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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;

import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.Button;
import org.zkoss.zul.Messagebox.ClickEvent;

/**
 * Used with {@link Messagebox} to implement a message box.
 *
 * @author tomyeh
 */
public class MessageboxDlg extends Window {
	/** What buttons are allowed. */
	private Button[] _buttons;
	/** Which button is pressed. */
	private Button _result;
	/** The event lisetener. */
	private EventListener<ClickEvent> _listener;

	public void onOK() throws Exception {
		if (contains(Button.OK)) endModal(Messagebox.ON_OK, Button.OK);
		else if (contains(Button.YES)) endModal(Messagebox.ON_YES, Button.YES);
		else if (contains(Button.RETRY)) endModal(Messagebox.ON_RETRY, Button.RETRY);
	}
	public void onCancel() throws Exception {
		if (_buttons.length == 1 && _buttons[0] == Button.OK) endModal(Messagebox.ON_OK, Button.OK);
		else if (contains(Button.CANCEL)) endModal(Messagebox.ON_CANCEL, Button.CANCEL);
		else if (contains(Button.NO)) endModal(Messagebox.ON_NO, Button.NO);
		else if (contains(Button.ABORT)) endModal(Messagebox.ON_ABORT, Button.ABORT);
	}
	private boolean contains(Button button) {
		for (int j = 0; j < _buttons.length; ++j)
			if (_buttons[j] == button)
				return true;
		return false;
	}

	/** Sets what buttons are allowed. */
	public void setButtons(Button[] buttons) {
		_buttons = buttons != null && buttons.length > 0 ? buttons: _defaultButtons;
		final Component parent = getFellow("buttons");
		final String sclass = (String)parent.getAttribute("button.sclass");
		for (int j = 0; j < _buttons.length; ++j) {
			if (_buttons[j] == null)
				throw new IllegalArgumentException("The "+j+"-th button is not specified");
			final MessageButton mbtn = new MessageButton(_buttons[j]);
			mbtn.setSclass(sclass);
			parent.appendChild(mbtn);
		}
	}
	private static final Button[] _defaultButtons = new Button[] {Button.OK};

	/** Sets the event listener.
	 * @param listener the event listener. If null, no invocation at all.
	 * @since 3.0.4
	 */
	public void setEventListener(EventListener<ClickEvent> listener) {
		_listener = listener;
	}
	/** Sets the focus.
	 * @param button the button to gain the focus. If 0, the default one
	 * (i.e., the first one) is assumed.
	 * @since 3.0.0
	 */
	public void setFocus(Button button) {
		if (button != null) {
			final MessageButton btn = (MessageButton)getFellowIfAny("btn" + button.value);
			if (btn != null)
				btn.focus();
		}
	}

	/** Called only internally.
	 */
	public void endModal(String evtnm, Button button) throws Exception {
		_result = button;
		if (_listener != null) {
			final ClickEvent evt = new ClickEvent(evtnm, this, button);
			_listener.onEvent(evt);
			if (!evt.isPropagatable())
				return; //no more processing
		}
		detach();
	}
	/** Returns the result which is the button being pressed.
	 */
	public Button getResult() {
		return _result;
	}

	//Override//
	public void onClose() {
		if (_listener != null) {
			final ClickEvent evt = new ClickEvent(Events.ON_CLOSE, this, null);
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
	public static class MessageButton extends org.zkoss.zul.Button {
		private final Button _button;
		private final String _evtnm;

		public MessageButton(Button button) {
			_button = button;

			final int label;
			switch (button.value) {
			case Messagebox.YES:
				label = MZul.YES;
				_evtnm = Messagebox.ON_YES;
				break;
			case Messagebox.NO:
				label = MZul.NO;
				_evtnm = Messagebox.ON_NO;
				break;
			case Messagebox.RETRY:
				label = MZul.RETRY;
				_evtnm = Messagebox.ON_RETRY;
				break;
			case Messagebox.ABORT:
				label = MZul.ABORT;
				_evtnm = Messagebox.ON_ABORT;
				break;
			case Messagebox.IGNORE:
				label = MZul.IGNORE;
				_evtnm = Messagebox.ON_IGNORE;
				break;
			case Messagebox.CANCEL:
				label = MZul.CANCEL;
				_evtnm = Messagebox.ON_CANCEL;
				break;
			default:
				label = MZul.OK;
				_evtnm = Messagebox.ON_OK;
				break;
			}
			setLabel(Messages.get(label));
			setId("btn" + _button.value);
		}
		public void onClick() throws Exception {
			((MessageboxDlg)getSpaceOwner()).endModal(_evtnm, _button);
		}
		protected String getDefaultMold(Class klass) {
			return super.getDefaultMold(org.zkoss.zul.Button.class);
		}
	}
}
