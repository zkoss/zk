/* URIChangedCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug  7 09:32:39     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.in;

import org.zkoss.lang.Objects;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.URIEvent;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;

/**
 * Used by {@link AuRequest} to implement a command to handle
 * an {@link URIEvent} event.
 *
 * @author tomyeh
 * @since 3.5.0
 */
public class URIChangedCommand extends Command {
	/** Contructs an event to denote that the associated URI is changed.
	 */
	public URIChangedCommand(String evtnm, int flags) {
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

		String uri = data[0];
		int urilen = uri != null ? uri.length(): 0;
		if (urilen > 0 && uri.charAt(0) == '/') {
			//Convert URL to URI if starting with the context path
			String ctx = Executions.getCurrent().getContextPath();
			int ctxlen = ctx != null ? ctx.length(): 0;
			if (ctxlen > 0 && !"/".equals(ctx)) {
				if (ctx.charAt(0) != '/') { //just in case
					ctx = '/' + ctx;
					++ctxlen;
				}
				if (uri.startsWith(ctx)
				&& (urilen == ctxlen || uri.charAt(ctxlen) == '/'))
					uri = uri.substring(ctxlen);
			}
		}
		Events.postEvent(new URIEvent(getId(), comp, uri));
	}
}
