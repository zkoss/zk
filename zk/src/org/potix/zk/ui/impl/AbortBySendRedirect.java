/* AbortBySendRedirect.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Apr 30 18:15:25     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.impl;

import com.potix.zk.au.AuResponse;
import com.potix.zk.au.AuSendRedirect;

/**
 * The aborting reason for send-redirect.
 * In other words, the client will redirect the specified URL.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
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
	public AuResponse getResponse() {
		return new AuSendRedirect(_url, _target);
	}
}
