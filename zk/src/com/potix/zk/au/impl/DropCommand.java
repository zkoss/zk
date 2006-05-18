/* DropCommand.java

{{IS_NOTE
	$Id: DropCommand.java,v 1.2 2006/02/27 03:54:45 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Mon Feb 27 00:01:36     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

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
import com.potix.zk.ui.event.DropEvent;
import com.potix.zk.au.AuRequest;

/**
 * Used only by {@link AuRequest} to implement the {@link DropEvent}
 * relevant command.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:54:45 $
 */
public class DropCommand extends AuRequest.Command {
	public DropCommand(String evtnm, boolean skipIfEverError) {
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

		Events.postEvent(new DropEvent(getId(), comp,
			request.getDesktop().getComponentByUuid(data[0])));
	}
}
