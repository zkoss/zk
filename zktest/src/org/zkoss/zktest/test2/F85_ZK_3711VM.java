/* F85_ZK_3711VM.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 26 14:12:44 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Collections;
import java.util.Map;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.HistoryPopState;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.HistoryPopStateEvent;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author rudyhuang
 */
public class F85_ZK_3711VM {
	private int tabIndex = 0;

	public int getTabIndex() {
		return tabIndex;
	}

	@HistoryPopState
	@NotifyChange("tabIndex")
	public void handleHistoryPopState(@ContextParam(ContextType.TRIGGER_EVENT) HistoryPopStateEvent event) {
		Clients.log("[By @HistoryPopState]");
		Map state = (Map) event.getState();
		Clients.log("State: " + state);
		Clients.log("Url: " + event.getUrl());

		if (state != null) {
			Integer page = (Integer) state.get("page");
			if (page == null) page = 1;
			tabIndex = page - 1;
		}
	}

	@Command
	@NotifyChange("tabIndex")
	public void goPage(@BindingParam("title") String title,
	                             @BindingParam("url") String url,
	                             @BindingParam("page") int page) {
		tabIndex = page - 1;
		pushHistoryState(title, url, page);
	}

	@Command
	public void pushHistoryState(@BindingParam("title") String title,
	                               @BindingParam("url") String url,
	                               @BindingParam("page") int page) {
		Desktop desktop = Executions.getCurrent().getDesktop();
		desktop.pushHistoryState(Collections.singletonMap("page", page), title, url);
	}

	@Command
	@NotifyChange("tabIndex")
	public void replacePage(@BindingParam("title") String title,
	                        @BindingParam("url") String url,
	                        @BindingParam("page") int page) {
		tabIndex = page - 1;
		replaceHistoryState(title, url, page);
	}

	private void replaceHistoryState(String title, String url, int page) {
		Desktop desktop = Executions.getCurrent().getDesktop();
		desktop.replaceHistoryState(Collections.singletonMap("page", page), title, url);
	}
}
