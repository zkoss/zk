/* AcegiAuthenticationEntryPoint.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Dec 27 11:32:06     2006, Created by Henri
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.acegi;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;

import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import org.zkoss.util.CollectionsX;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>The default listener to show a modal window for login and other things.</p>
 * <p>The event data is the url page and it expects:</p>
 * <ul>
 * <li>The url page must be a zul defined page (*.zul).</li>
 * <li>The url page must enclosed with a window component so it can be doModal().</li>
 * </ul>
 * <p>This implementation would automatically remove the added eventlistener from the target component.</p>
 *
 * @author Henri
 */
public class ShowWindowEventListener implements EventListener {
	public void onEvent(Event event) {
		//fetch old Event stored in Session and post again
		final Component comp = event.getTarget();
		
		final Page page = comp.getPage();
		final String url = (String) event.getData();
		final Execution exec = Executions.getCurrent();
		try {
			doModal(page, url);
    	} finally {
			if (comp.isListenerAvailable(event.getName(), true)) {
				EventListener listener = (EventListener) comp.getAttribute(event.getName());
				if (listener != null) {
					comp.removeEventListener(event.getName(), listener);
					comp.removeAttribute(event.getName());
				}
			}
		}
	}

	private void doModal(Page page, String url) {
		final Execution exec = Executions.getCurrent();
		final Object[] urls = parseUrl(url);
    	final Component modalwin = exec.createComponents((String)urls[0], null, (Map)urls[1]);
    	if (!(modalwin instanceof Window)) {
    		throw new UiException("The page must enclosed with a Window component. Check url definition: "+url);
    	}
    	modalwin.setPage(page);
    	try {
	    	((Window)modalwin).doModal();
    	} catch(java.lang.InterruptedException ex) {
    		//ignore
    	}
	}		

	//Object[0]: url, Object[1]: Map
	private Object[] parseUrl(String url) {
		Object[] result = new Object[2];
		int j = url.indexOf("?");
		if (j < 0) {
			result[0] = url;
			return result;
		}
		result[0] = url.substring(0, j);
		
		if ((j+1) >= url.length()) {
			return result;
		}
		
		url = url.substring(j+1);
		List<String> list = new LinkedList<String>();
		CollectionsX.parse(list, url, '&');
		Map<String, String> args = new HashMap<String, String>();
		for(String s: list) {
			List<String> pair = new ArrayList<String>(2);
			CollectionsX.parse(pair, s, '=');
			args.put(pair.get(0), pair.get(1));
		}
		
		result[1] = args;
		return result;
	}
}
