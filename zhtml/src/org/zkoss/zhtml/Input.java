/* Input.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Nov 29 21:59:11     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import java.lang.Object; //since we have org.zkoss.zhtml.Object

import org.zkoss.lang.Objects;

import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zhtml.impl.AbstractTag;

/**
 * The input tag.
 *
 * @author tomyeh
 */
public class Input extends AbstractTag {
	private transient boolean _byClient;

	static {
		addClientEvent(Input.class, Events.ON_CHANGE, 0);
		addClientEvent(Input.class, Events.ON_CHECK, 0);
	}

	public Input() {
		this("input");
	}
	protected Input(String tagnm) {
		super(tagnm);
		setValue("");
	}

	/** Returns the value of this input.
	 */
	public String getValue() {
		return (String)getDynamicProperty("value");
	}
	/** Sets the vallue of this input.
	 */
	public void setValue(String value) throws WrongValueException {
		setDynamicProperty("value", value);
	}

	/** Returns if the input is checked (type: checkbox or radio).
	 */
	public boolean isChecked() {
		final Boolean b = (Boolean)getDynamicProperty("checked");
		return b != null && b.booleanValue();
	}
	/** Sets if the input is checked (type: checkbox or radio).
	 */
	public void setChecked(boolean checked) {
		setDynamicProperty("checked", Boolean.valueOf(checked));
	}

	//super//
	protected void smartUpdate(String attr, Object value) {
		if (!_byClient)
			super.smartUpdate(attr, value);
	}

	/** Processes an AU request.
	 *
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_CHANGE)) {
			InputEvent evt = InputEvent.getInputEvent(request);

			final String value = evt.getValue();
			_byClient = true;
			try {
				setValue(value);
			} finally {
				_byClient = false;
			}

			Events.postEvent(evt);
		} else if (cmd.equals(Events.ON_CHECK)) {
			CheckEvent evt = CheckEvent.getCheckEvent(request);

			_byClient = true;
			try {
				setChecked(evt.isChecked());
			} finally {
				_byClient = false;
			}

			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}
}
