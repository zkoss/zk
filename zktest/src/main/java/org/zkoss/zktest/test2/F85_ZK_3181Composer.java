/* F85_ZK_3181Composer.java

	Purpose:
		
	Description:
		
	History:
		Mon May 28 15:37:13 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;

/**
 * @author rudyhuang
 */
public class F85_ZK_3181Composer extends SelectorComposer<Div> {
	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);

		comp.addEventListener(
			Events.ON_CLIENT_INFO,
			new EventListener<ClientInfoEvent>() {
				@Override
				public void onEvent(ClientInfoEvent event) throws Exception {
					Clients.log("Received onClientInfo event");
				}
			}
		);
	}
}
