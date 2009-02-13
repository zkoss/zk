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

import org.zkoss.zk.ui.Component;

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
 * @see org.zkoss.zk.ui.ext.client.Openable
 */
public class OpenEvent extends Event {
	private final boolean _open;
	private final Component _ref;

	/** Constructs an onOpen event.
	 * @param open whether the new status is open
	 */
	public OpenEvent(String name, Component target, boolean open) {
		this(name, target, open, null);
	}
	/** Constructs an onOpen event for a context menu, a tooltip or a popup.
	 *
	 * @param target the component being opened
	 * @param ref the component that causes target to be opened.
	 */
	public OpenEvent(String name, Component target, boolean open,
	Component ref) {
		super(name, target);
		_open = open;
		_ref = ref;
	}
	/** Returns the reference that is the component causing {@link #getTarget}
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
	public final boolean isOpen() {
		return _open;
	}
}
