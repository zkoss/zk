/** ClientBinderPhaseListener.java.

	Purpose:
		
	Description:
		
	History:
		2:43:54 PM Jan 28, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Phase;
import org.zkoss.bind.PhaseListener;
import org.zkoss.bind.annotation.ToClientCommand;
import org.zkoss.json.JavaScriptValue;
import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zk.ui.util.Clients;

/**
 * A client command binding phase listener.
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
			final Binder binder = ctx.getBinder();
			final Object vm = binder.getViewModel();
			if (binder instanceof BinderImpl && vm != null) {
				ToClientCommand ccmd = vm.getClass().getAnnotation(ToClientCommand.class);
				if (ccmd != null) { 
					List<String> asList = Arrays.asList(ccmd.value());
				
					if (asList.contains("*") || asList.contains(commandName)) {
						final Map<String, Object> args = (Map<String, Object>) ctx.getAttribute(BindContextImpl.COMMAND_ARGS);
						if (args != null) {
							if (args.size() == 1) {
								Object data = new JavaScriptValue(
										String.valueOf(binder.getConverter(
												"jsonBindingParam").coerceToUi(
												args.values().iterator().next(),
												ctx.getComponent(), ctx)));
								Clients.response(new AuInvoke(ctx.getBinder()
										.getView(), "$afterCommand", new Object[] {
										commandName, data }));
							} else {
								Object data = new JavaScriptValue(String.valueOf(binder.getConverter("jsonBindingParam").coerceToUi(args, ctx.getComponent(), ctx)));
								Clients.response(new AuInvoke(ctx.getBinder().getView(), "$afterCommand", new Object[]{commandName, 
										data}));
							}
						} else {
							Clients.response(new AuInvoke(ctx.getBinder().getView(), "$afterCommand", commandName));
						}
					}
				}
			}
			break;
		}
	}
}
