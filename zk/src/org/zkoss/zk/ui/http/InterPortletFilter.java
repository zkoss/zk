/* InterPortletFilter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Dec  7 15:38:47     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

/**
 * Filter used to communicate among portlets for the purpose of ensuring only one
 * desktop per HTTP request in any portal container.
 *
 * <p>By default, a portlet cannot communicate any other portlet, at least,
 * thru the request's attributes.
 * On the other hand, ZK uses the request's attributes to ensure there is only
 * one desktop per HTTP request, no matter how many ZUML pages are rendered.
 * To resolve this issue, you can map portal to this filter as described
 * in the developer's guide.
 *
 * @author tomyeh
 */
public class InterPortletFilter implements Filter {
	private ThreadLocal _first;

	public void doFilter(ServletRequest request, ServletResponse response,
	FilterChain chain) throws IOException, ServletException {
		final boolean first = _first.get() == null;
		if (first) {
			WebManager.initRequestLocal();
			_first.set(Boolean.TRUE);
		}
		try {
			chain.doFilter(request, response);
		} finally {
			if (first) {
				_first.set(null);
				WebManager.cleanRequestLocal();
			}
		}
	}
	public void destroy() {
	}
	public final void init(FilterConfig config) throws ServletException {
	}
}
