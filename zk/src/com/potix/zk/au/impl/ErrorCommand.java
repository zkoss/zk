/* ErrorCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 21 15:58:32     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

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
import com.potix.zk.ui.event.ErrorEvent;
import com.potix.zk.ui.ext.Errorable;
import com.potix.zk.au.AuRequest;

/**
 * Used only by {@link AuRequest} to implement the {@link ErrorEvent}
 * relevant command.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class ErrorCommand extends AuRequest.Command {
	public ErrorCommand(String evtnm, boolean skipIfEverError) {
		super(evtnm, skipIfEverError);
	}

	//-- super --//
	protected void process(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);
		final String[] data = request.getData();
		if (data == null || data.length != 2)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), this});

		final String newval = data[0], msg = data[1];
		((Errorable)comp).setErrorByClient(newval, msg);
		Events.postEvent(new ErrorEvent(getId(), comp, newval, msg));
	}
}
