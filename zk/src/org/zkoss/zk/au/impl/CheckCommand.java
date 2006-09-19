/* CheckCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Oct  2 13:14:29     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.impl;

import org.zkoss.lang.Objects;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.ext.Checkable;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;

/**
 * Used only by {@link AuRequest} to implement the {@link CheckEvent}
 * relevant command.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class CheckCommand extends Command {
	public CheckCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	//-- super --//
	protected void process(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);
		final String[] data = request.getData();
		if (data == null || data.length != 1)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), this});

		final boolean checked = "true".equals(data[0]);
		((Checkable)comp).setCheckedByClient(checked);
		Events.postEvent(new CheckEvent(getId(), comp, checked));
	}
}
