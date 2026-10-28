/* PreviewPathFilter.java

	Purpose:
		Serve the Marble preview pages at the context root.
	Description:
		The pages live under /web (execution plan D44) so that their class-web-resource references
		(~./...) resolve. The Playwright harness copied from the theme template navigates with a leading
		slash (/button.zul), so this filter forwards /<page>.zul to /web/<page>.zul whenever that page
		exists, and the module answers exactly where the template's preview app answered (D47).
	History:
		Thu Sep 10 2026, Created for ZK-6112.

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zkthemelab.http;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class PreviewPathFilter implements Filter {
	private ServletContext _ctx;

	public void init(FilterConfig config) {
		_ctx = config.getServletContext();
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		final String path = ((HttpServletRequest) request).getServletPath();
		if (path != null && !path.startsWith("/web/") && _ctx.getResource("/web" + path) != null) {
			request.getRequestDispatcher("/web" + path).forward(request, response);
			return;
		}
		chain.doFilter(request, response);
	}
}
