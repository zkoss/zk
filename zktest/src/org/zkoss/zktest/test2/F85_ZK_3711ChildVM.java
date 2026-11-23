/* F85_ZK_3711VM.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 26 14:12:44 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.HistoryPopState;
import org.zkoss.zk.ui.event.HistoryPopStateEvent;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author rudyhuang
 */
public class F85_ZK_3711ChildVM extends F85_ZK_3711VM {
	@HistoryPopState
	public void handleHistoryPopState(@ContextParam(ContextType.TRIGGER_EVENT) HistoryPopStateEvent event) {
		Clients.log(event.getUrl());
	}

	@HistoryPopState
	public void handleHistoryPopState2(@ContextParam(ContextType.TRIGGER_EVENT) HistoryPopStateEvent event) {
		Clients.log(event.getUrl());
	}
}
