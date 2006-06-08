/* OpenEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul  8 17:00:03     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.event;

import com.potix.zk.ui.Component;

/**
 * Represents an event cause by user's openning or closing
 * something at the client.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @see com.potix.zk.ui.ext.Openable
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
