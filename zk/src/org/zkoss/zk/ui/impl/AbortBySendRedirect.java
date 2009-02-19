/* AbortBySendRedirect.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Apr 30 18:15:25     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.out.AuSendRedirect;
import org.zkoss.zk.ui.sys.AbortingReason;

/**
 * The aborting reason for send-redirect.
 * In other words, the client will redirect the specified URL.
 *
 * @author tomyeh
 */
public class AbortBySendRedirect implements AbortingReason {
	private final String _url;
	private final String _target;

	/** Constructs an aborting reason for send-redirect.
	 *
	 * @param url the target URL, never null.
	 * @param target the target browser window, or null to use the current one.
	 */
	public AbortBySendRedirect(String url, String target) {
		if (url == null) throw new IllegalArgumentException("null");
		_url = url;
		_target = target;
	}

	//-- AbortingReason --//
	public boolean isAborting() {
		return !_url.startsWith("mailto:") && !_url.startsWith("javascript:")
			&& (_target == null || "_self".equals(_target));
	}
	public void execute() {
	}
	public AuResponse getResponse() {
		return new AuSendRedirect(_url, _target);
	}
	public void finish() {
	}
}
