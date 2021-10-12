/* EmbeddedCorsFilter.java

	Purpose:

	Description:

	History:
		Wed Oct 29 12:26:36 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.http;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Used to handle cross domain request.
 * <p>How to use:
 * <li>Maps the servlet to the filter in web.xml</li>
 * </ul>
 *
 * @author jameschu
 */
public class EmbeddedCorsFilter implements Filter {
	public EmbeddedCorsFilter() {

	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		// Authorize (allow) all domains to consume the content
		HttpServletResponse res = (HttpServletResponse) servletResponse;
		res.addHeader("Access-Control-Allow-Origin", "http://localhost:9000");
		res.addHeader("Access-Control-Allow-Credentials", "true");
		res.addHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");
		res.addHeader("Access-Control-Allow-Headers", "ZK-SID,ZK-Error");
		res.addHeader("Access-Control-Expose-Headers", "ZK-SID,ZK-Error");
		// For HTTP OPTIONS, reply with ACCEPTED status code.
		if (request.getMethod().equals("OPTIONS")) {
			res.setStatus(HttpServletResponse.SC_ACCEPTED);
			return;
		}
		chain.doFilter(request, servletResponse);
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

	public void destroy() {
	}
}