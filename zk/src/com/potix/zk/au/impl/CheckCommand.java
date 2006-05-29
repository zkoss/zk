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
package com.potix.zk.au.impl;

import com.potix.lang.Objects;

import com.potix.zk.mesg.MZk;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.event.Events;
import com.potix.zk.ui.event.CheckEvent;
import com.potix.zk.ui.ext.Checkable;
import com.potix.zk.au.AuRequest;

/**
 * Used only by {@link AuRequest} to implement the {@link CheckEvent}
 * relevant command.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class CheckCommand extends AuRequest.Command {
	public CheckCommand(String evtnm, boolean skipIfEverError) {
		super(evtnm, skipIfEverError);
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
