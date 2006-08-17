/* ClientInfoCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 28 14:13:09     2006, Created by tomyeh@potix.com
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
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.event.Events;
import com.potix.zk.ui.event.ClientInfoEvent;
import com.potix.zk.au.AuRequest;
import com.potix.zk.au.Command;

/**
 * Used by {@link AuRequest} to implement a command to broadcast
 * an {@link ClientInfoEvent} event to all root components.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class ClientInfoCommand extends Command {
	public ClientInfoCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	//-- super --//
	protected void process(AuRequest request) {
		final String[] data = request.getData();
		if (data == null || data.length != 8)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), this});
		Events.postEvent(new ClientInfoEvent(getId(),
			Integer.parseInt(data[0]), Integer.parseInt(data[1]),
			Integer.parseInt(data[2]), Integer.parseInt(data[3]),
			Integer.parseInt(data[4]), Integer.parseInt(data[5]),
			Integer.parseInt(data[6]), Integer.parseInt(data[7])));
	}
}
