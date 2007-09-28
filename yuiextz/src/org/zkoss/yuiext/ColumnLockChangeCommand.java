/* ColumnLockChangeCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed July 4 18:41:32     2007, Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.yuiext;

import java.util.Set;

import org.zkoss.yuiext.event.ColumnLockChangeEvent;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.client.Selectable;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;
import org.zkoss.zk.au.impl.Commands;

/** 
 * Used only by {@link AuRequest} to implement the {@link ColumnLockChangeEvent}
 * relevant command.
 * @author  jumperchen
 */
public class ColumnLockChangeCommand extends Command {
	public ColumnLockChangeCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	//-- super --//
	protected void process(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);
		final Set items = Commands.convertToItems(request);
		((Selectable)((ComponentCtrl)comp).getExtraCtrl())
			.selectItemsByClient(items);
		Events.postEvent(new ColumnLockChangeEvent(getId(), comp, items));
	}
}
