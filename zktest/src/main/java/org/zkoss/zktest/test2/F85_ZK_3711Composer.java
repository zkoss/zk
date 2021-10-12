/* F85_ZK_3711Composer.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul 27 15:11:23 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.event.HistoryPopStateEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

/**
 * @author rudyhuang
 */
public class F85_ZK_3711Composer extends SelectorComposer<Window> {
	private int position = 1;

	@Listen("#btnHistoryAdd")
	public void addHistory() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		int position = this.position++;
		map.put("f1", position);
		map.put("f2", (int) System.currentTimeMillis());
		getSelf().getDesktop().pushHistoryState(map, "", "?p" + position);
	}

	@Listen("#btnHistoryReplace")
	public void replaceHistory() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		int position = this.position++;
		map.put("f1", position);
		map.put("f2", (int) System.currentTimeMillis());
		getSelf().getDesktop().replaceHistoryState(map, "", "?p" + position);
	}

	@Listen("onHistoryPopState = #win")
	public void handleHistoryPopState(HistoryPopStateEvent event) {
		Map<String, ?> state = (Map<String, ?>) event.getState();
		if (state != null) {
			Clients.log(state.toString() + ", " + event.getUrl());
		}
	}
}
