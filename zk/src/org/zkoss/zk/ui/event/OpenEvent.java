/* OpenEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul  8 17:00:03     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import org.zkoss.lang.Objects;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;

/**
 * Represents an event cause by user's openning or closing
 * something at the client.
 *
 * <p>Note: it is a bit confusing but {@link Events#ON_CLOSE} is sent when
 * user clicks a close button. It is a request to ask the server
 * to close a window, a tab or others. If the server ignores the event,
 * nothing will happen at the client. By default, the component is
 * detached when receiving this event.
 *
 * <p>On the other hand, {@link Events#ON_OPEN} (with {@link OpenEvent}) is
 * a notification. It is sent to notify the server that the client has
 * opened or closed something.
 * And, the server can not prevent the client from opening or closing.
 * 
 * @author tomyeh
 */
public class OpenEvent extends Event {
	private final boolean _open;
	private final Component _ref;
	private final Object _val;

	/** Converts an AU request to an open event.
	 * @since 5.0.0
	 */
	public static final OpenEvent getOpenEvent(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, request);
		final String[] data = request.getData();
		if (data == null || data.length < 1 || data.length > 3)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), request});

		final boolean open = "true".equals(data[0]);
		final Component ref = data.length >= 2 && data[1] != null ?
			request.getDesktop().getComponentByUuidIfAny(data[1]): null;
		return new OpenEvent(request.getName(), comp, open, ref,
			data.length == 3 ? data[2]: null);
			//FUTURE: support non-String value (by coerce to comp.value.class)
	}

	/** Constructs an onOpen event.
	 * @param open whether the new status is open
	 */
	public OpenEvent(String name, Component target, boolean open) {
		this(name, target, open, null, null);
	}
	/** Constructs an onOpen event for a context menu, a tooltip or a popup.
	 *
	 * @param target the component being opened
	 * @param ref the component that causes target to be opened.
	 */
	public OpenEvent(String name, Component target, boolean open,
	Component ref) {
		this(name, target, open, ref, null);
	}
	/** Constructs an onOpen event.
	 * @param open whether the new status is open
	 * @param value the current value of the target component if applicable.
	 * @see #getValue
	 * @since 3.5.0
	 */
	public OpenEvent(String name, Component target, boolean open,
	Component ref, Object value) {
		super(name, target);
		_open = open;
		_ref = ref;
		_val = value;
	}
	/** Returns the reference that causes {@link #getTarget}
	 * to be opened.
	 *
	 * <p>It is null, if the open event is not caused by opening
	 * a context menu, a tooltip or a popup.
	 * Note: the onOpen event is also sent when closing the context menu
	 * (tooltip and popup), and this method returns null in this case.
	 * Thus, it is better to test {@link #isOpen} or {@link #getReference}
	 * before accessing the returned value.
	 *
	 * <code>if (event.isOpen()) doSome(event.getReference());</code>
	 */
	public Component getReference() {
		return _ref;
	}
	/** Returns whether it causes open.
	 */
	public boolean isOpen() {
		return _open;
	}
	/** Returns the value of the target component,
	 * when the onOpen event is sent, or null if not applicable.
	 *
	 * <p>Note: combobox, bandbox and other combo-type input don't
	 * send the onChange event when the dropdown is opened (onOpen).
	 * Thus, if you want to do something depends on the value,
	 * use the value returned by this method.
	 * Furthermore, for combobox and bandbox, the return value is
	 * a non-null String instance.
	 *
	 * @since 3.5.0
	 */
	public Object getValue() {
		return _val;
	}
}
