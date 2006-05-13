/* ScrollCommand.java

{{IS_NOTE
	$Id: ScrollCommand.java,v 1.4 2006/03/31 03:20:38 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Sun Oct  2 21:30:04     2005, Created by tomyeh@potix.com
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
import com.potix.zk.ui.event.ScrollEvent;
import com.potix.zk.ui.ext.Scrollable;
import com.potix.zk.au.AuRequest;

/**
 * Used only by {@link AuRequest} to implement the {@link ScrollEvent}
 * relevant command.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.4 $ $Date: 2006/03/31 03:20:38 $
 */
public class ScrollCommand extends AuRequest.Command {
	public ScrollCommand(String evtnm, boolean skipIfEverError) {
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

		final int newpos = Integer.parseInt(data[0]);
		if (this == AuRequest.ON_SCROLL)
			((Scrollable)comp).setCurposByClient(newpos);
		Events.postEvent(new ScrollEvent(getId(), comp, newpos));
	}
}
