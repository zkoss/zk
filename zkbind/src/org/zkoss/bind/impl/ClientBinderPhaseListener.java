/** ClientBinderPhaseListener.java.

	Purpose:
		
	Description:
		
	History:
		2:43:54 PM Jan 28, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Phase;
import org.zkoss.bind.PhaseListener;
import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zk.au.out.AuSetAttribute;
import org.zkoss.zk.ui.util.Clients;

/**
 * A client command binding phase listener
 * @author jumperchen
 * @since 8.0.0
 */
public class ClientBinderPhaseListener implements PhaseListener {

	public void prePhase(Phase phase, BindContext ctx) {
	}

	/* (non-Javadoc)
	 * @see org.zkoss.bind.PhaseListener#postPhase(org.zkoss.bind.Phase, org.zkoss.bind.BindContext)
	 */
	public void postPhase(Phase phase, BindContext ctx) {
		switch (phase) {
		case COMMAND:
			final String commandName = ctx.getCommandName();
			Clients.response(new AuInvoke(ctx.getBinder().getView(), "$afterCommand", commandName));
			break;
		}
	}
}
