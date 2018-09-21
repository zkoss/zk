/* B86_ZK_4061VM.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 19 10:35:46 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.concurrent.atomic.AtomicInteger;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.HistoryPopState;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.event.HistoryPopStateEvent;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author rudyhuang
 */
public class B86_ZK_4061VM {
	private final AtomicInteger historyIndex = new AtomicInteger();

	@Command
	public void pushHistoryState(@ContextParam(ContextType.DESKTOP) Desktop desktop) {
		final int index = historyIndex.incrementAndGet();
		desktop.pushHistoryState(String.valueOf(index), "", "#" + index);
	}

	@HistoryPopState
	public void handleHistoryPopState(@ContextParam(ContextType.TRIGGER_EVENT) HistoryPopStateEvent event) {
		Clients.log(event.getState());
	}
}
