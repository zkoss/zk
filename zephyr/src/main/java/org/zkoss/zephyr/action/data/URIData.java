/* URIData.java

	Purpose:

	Description:

	History:
		12:51 PM 2022/3/23, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.action.data;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

import org.zkoss.zk.ui.Executions;

/**
 * The URI update event used with <code>onURIChange</code>
 * to notify that the associated URI is changed by the user.
 *
 * <p>A typical use of this action is to support a better bookmarking
 * for a page containing iframe.
 *
 * @author jumperchen
 */
public class URIData implements ActionData {
	private String uri;

	@JsonCreator
	private URIData(Map data) {
		String uri = (String) data.get("");
		int urilen = uri.length();
		if (urilen > 0 && uri.charAt(0) == '/') {
			//Convert URL to URI if starting with the context path
			String ctx = Executions.getCurrent().getContextPath();
			int ctxlen = ctx != null ? ctx.length() : 0;
			if (ctxlen > 0 && !"/".equals(ctx)) {
				if (ctx.charAt(0) != '/') { //just in case
					ctx = '/' + ctx;
					++ctxlen;
				}
				if (uri.startsWith(ctx) && (urilen == ctxlen || uri.charAt(ctxlen) == '/'))
					uri = uri.substring(ctxlen);
			}
		}
		this.uri = uri;
	}

	/** Returns the URI (never null).
	 * Notice that it does not include the context path, unless
	 * it starts with a protocol (such as http://).
	 */
	public String getURI() {
		return uri;
	}
}
