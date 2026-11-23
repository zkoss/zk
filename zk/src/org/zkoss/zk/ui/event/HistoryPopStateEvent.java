/* PopStateEvent.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 26 11:22:24 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.event;

import java.util.Map;

import org.zkoss.zk.au.AuRequest;

/** The history pop state event used with <code>onHistoryPopState</code>
 * to notify that user pressed BACK, FORWARD or others
 * that causes the history changed (but still in the same desktop).
 *
 * <p>All root components of all pages of the desktop will
 * receives this event.
 *
 * @author rudyhuang
 * @since 8.5.0
 */
public class HistoryPopStateEvent extends Event {
	private final Object _state;
	private final String _url;

	/**
	 * Converts an AU request to a history pop state event.
	 */
	public static HistoryPopStateEvent getHistoryPopStateEvent(AuRequest request) {
		final Map<String, Object> data = request.getData();
		return new HistoryPopStateEvent(
				request.getCommand(),
				data.get("state"),
				(String) data.get("url")
		);
	}

	public HistoryPopStateEvent(String name, Object state) {
		this(name, state, null);
	}

	public HistoryPopStateEvent(String name, Object state, String url) {
		super(name);
		this._state = state;
		this._url = url;
	}

	/**
	 * Returns the state object.
	 * @return the state object.
	 */
	public Object getState() {
		return _state;
	}

	/**
	 * Returns the URL.
	 * @return the URL.
	 */
	public String getUrl() {
		return _url;
	}
}
