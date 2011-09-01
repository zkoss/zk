/* RequestScope.java

	Purpose:
		
	Description:
		
	History:
		Mon Sep 21 14:00:12     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.web.servlet.xel;

import java.util.Enumeration;
import javax.servlet.ServletRequest;

/**
 * Represents the request scope.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class RequestScope extends AttributesMap {
	private final ServletRequest _request;

	public RequestScope(ServletRequest request) {
		_request = request;
	}

	protected Enumeration getKeys() {
		return _request.getAttributeNames();
	}
	protected Object getValue(String key) {
		return _request.getAttribute(key);
	}
	protected void setValue(String key, Object val) {
		_request.setAttribute(key, val);
	}
	protected void removeValue(String key) {
		_request.removeAttribute(key);
	}
}
