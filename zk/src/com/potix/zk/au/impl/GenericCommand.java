/* GenericCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Oct  2 13:34:19     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.au.impl;

import com.potix.zk.mesg.MZk;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.event.Events;
import com.potix.zk.ui.event.Event;
import com.potix.zk.au.AuRequest;

/**
 * Used by {@link AuRequest} to implement generic command
 * that does nothing but posting an {@link Event}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class GenericCommand extends AuRequest.Command {
	private final boolean _broadcast;
	public GenericCommand(String evtnm, boolean skipIfEverError) {
		super(evtnm, skipIfEverError);
		_broadcast = false;
	}
	/**
	 * @param broadcast whether to broadcast the event to all root components
	 * of all pages in the same desktop, if component is null.
	 */
	public GenericCommand(String evtnm, boolean skipIfEverError, boolean broadcast) {
		super(evtnm, skipIfEverError);
		_broadcast = broadcast;
	}

	//-- super --//
	protected void process(AuRequest request) {
		final Component comp = request.getComponent();
		if (!_broadcast && comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);
		final String[] data = request.getData();
		Events.postEvent(
			data == null || data.length == 0 ? new Event(getId(), comp):
			data.length == 1 ? new Event(getId(), comp, data[0]):
				new Event(getId(), comp, data));
	}
}
