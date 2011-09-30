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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;

import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;

/**
 * Used with {@link Messagebox} to implement a message box.
 *
 * @author tomyeh
 */
public class MessageboxDlg extends Window {
	/** What buttons are allowed. */
	private Messagebox.Button[] _buttons;
	/** Which button is pressed. */
	private Messagebox.Button _result;
	/** The event lisetener. */
	private EventListener<ClickEvent> _listener;

	public void onOK() throws Exception {
		if (contains(Messagebox.Button.OK)) endModal(Messagebox.Button.OK);
		else if (contains(Messagebox.Button.YES)) endModal(Messagebox.Button.YES);
		else if (contains(Messagebox.Button.RETRY)) endModal(Messagebox.Button.RETRY);
	}
	public void onCancel() throws Exception {
		if (_buttons.length == 1 && _buttons[0] == Messagebox.Button.OK) endModal(Messagebox.Button.OK);
		else if (contains(Messagebox.Button.CANCEL)) endModal(Messagebox.Button.CANCEL);
		else if (contains(Messagebox.Button.NO)) endModal(Messagebox.Button.NO);
		else if (contains(Messagebox.Button.ABORT)) endModal(Messagebox.Button.ABORT);
	}
	private boolean contains(Messagebox.Button button) {
		for (int j = 0; j < _buttons.length; ++j)
			if (_buttons[j] == button)
				return true;
		return false;
	}

	/** Sets what buttons are allowed. */
	public void setButtons(Messagebox.Button[] buttons) {
		_buttons = buttons;

		final Component parent = getFellow("buttons");
		if (parent.getFirstChild() == null) {
		//Backward compatible to ZK 5
		//We check if any child since user's old tempalte might create them
			final String sclass = (String)parent.getAttribute("button.sclass");
			for (int j = 0; j < _buttons.length; ++j) {
				final MButton mbtn = new MButton();
				mbtn.setButton(_buttons[j]);
				mbtn.setSclass(sclass);
				parent.appendChild(mbtn);
			}
		}
	}

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
	public void setFocus(Messagebox.Button button) {
		if (button != null) {
			final MButton btn = (MButton)getFellowIfAny("btn" + button.id);
			if (btn != null)
				btn.focus();
		}
	}

	/** Called only internally.
	 */
	public void endModal(Messagebox.Button button) throws Exception {
		_result = button;
		if (_listener != null) {
			final ClickEvent evt = new ClickEvent(button.event, this, button);
			_listener.onEvent(evt);
			if (!evt.isPropagatable())
				return; //no more processing
		}
		detach();
	}
	/** Returns the result which is the button being pressed.
	 */
	public Messagebox.Button getResult() {
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
	 * @since 6.0.0
	 */
	public static class MButton extends org.zkoss.zul.Button {
		private Messagebox.Button _button;

		public void setButton(Messagebox.Button button) {
			_button = button;
			setLabel(Messages.get(_button.label));
			setId("btn" + _button.id);
		}
		public void onClick() throws Exception {
			((MessageboxDlg)getSpaceOwner()).endModal(_button);
		}
		protected String getDefaultMold(Class klass) {
			return super.getDefaultMold(org.zkoss.zul.Button.class);
		}
	}
	/** @deprecated As of release 6.0.0, replaced with {@link MButton}.
	 */
	public static class Button extends MButton {
		/** Sets the identity.
		 */
		public void setIdentity(int button) {
			switch (button) {
			case Messagebox.YES:
				setButton(Messagebox.Button.YES);
				break;
			case Messagebox.NO:
				setButton(Messagebox.Button.NO);
				break;
			case Messagebox.RETRY:
				setButton(Messagebox.Button.RETRY);
				break;
			case Messagebox.ABORT:
				setButton(Messagebox.Button.ABORT);
				break;
			case Messagebox.IGNORE:
				setButton(Messagebox.Button.IGNORE);
				break;
			case Messagebox.CANCEL:
				setButton(Messagebox.Button.CANCEL);
				break;
			default:
				setButton(Messagebox.Button.OK);
				break;
			}
		}
	}
}
