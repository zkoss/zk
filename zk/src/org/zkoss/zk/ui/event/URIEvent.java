/* URIEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug  7 09:19:15     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import org.zkoss.json.JSONObject;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;

/**
 * The URI update event used with <code>onURIChange</code>
 * to notify that the assoicated URI
 * is changed by the user. Currently it is supported only by ZUL's
 * iframe component (and only if iframe contains a ZK page).
 *
 * <p>A typical use of this event is to support a better bookmarking
 * for a page containing iframe.
 *
 * <p>Unlike {@link BookmarkEvent}, this event is sent to the component
 * (iframe) directly.
 *
 * @author tomyeh
 * @since 3.5.0
 */
public class URIEvent extends Event {
	/** The URI. */
	private final String _uri;

	/** Converts an AU request to an URI event.
	 * @since 5.0.0
	 */
	public static final URIEvent getURIEvent(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, request);
		final JSONObject data = request.getData();
		if (data == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {data, request});

		String uri;
		try {
			uri = data.getString("");
		} catch (org.zkoss.json.JSONException ex) {
			throw new UiException(ex);
		}

		int urilen = uri.length();
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

		return new URIEvent(request.getCommand(), comp, uri);
	}

	/** Constructs an URI update event.
	 * @param target the component to receive the event.
	 * @param uri the URI. Note: it doesn't include the context path
	 * unless it starts with a protocol (such as http://).
	 */
	public URIEvent(String name, Component target, String uri) {
		super(name, target);
		_uri = uri != null ? uri: "";
	}

	/** Returns the URI (never null).
	 * Notice that it does not include the context path, unless
	 * it starts with a protocol (such as http://).
	 */
	public String getURI() {
		return _uri;
	}
}
