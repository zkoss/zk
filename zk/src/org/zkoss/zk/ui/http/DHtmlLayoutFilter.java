/* DHtmlLayoutFilter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Nov 30 00:49:59     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import java.io.StringWriter;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.util.logging.Log;
import org.zkoss.web.servlet.BufferedResponse;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.impl.RequestInfoImpl;

/**
 * Used to post-process the response into a ZK page.
 * It is useful if you have a servlet using servlets, JSP or other
 * technology.
 *
 * <p>How to use:
 * <ul>
 * <li>Decorates the servlet to embed the ZK components and
 * attributes, such as zk:onClick="...".</li>
 * <li>Maps the servlet to the filter in web.xml</li>
 * </ul>
 *
 * @author tomyeh
 */
public class DHtmlLayoutFilter implements Filter {
	private static final Log log = Log.lookup(DHtmlLayoutFilter.class);

	private ServletContext _ctx;
	private String _ext = "html";
	private String _charset = "UTF-8";

	private void process(HttpServletRequest request,
	HttpServletResponse response, String content)
	throws ServletException, IOException {
		if (log.debugable()) log.debug("Content to filter:\n"+content);

		final WebManager webman = WebManager.getWebManager(_ctx);
		final WebApp wapp = webman.getWebApp();
		final WebAppCtrl wappc = (WebAppCtrl)wapp;
		final Session sess = webman.getSession(_ctx, request);
		final Object old = I18Ns.setup(sess, request, response, _charset);
		try {
			final Desktop desktop = webman.getDesktop(sess, request, null);
			final RequestInfo ri = new RequestInfoImpl(
				wapp, sess, desktop, request, null);
			final UiFactory uf = wappc.getUiFactory();
			final PageDefinition pagedef =
				uf.getPageDefinitionDirectly(ri, content, _ext);

			final Page page = uf.newPage(ri, pagedef, null);
			final Execution exec =
				new ExecutionImpl(_ctx, request, response, desktop, page);
			wappc.getUiEngine()
				.execNewPage(exec, pagedef, page, response.getWriter());
		} finally {
			I18Ns.cleanup(request, old);
		}
	}

	//-- Filter --//
	public void doFilter(ServletRequest request, ServletResponse response,
	FilterChain chain) throws IOException, ServletException {
		final StringWriter sw = new StringWriter(4096*2);
		chain.doFilter(request,
			BufferedResponse.getInstance(response, sw));

		process((HttpServletRequest)request,
			(HttpServletResponse)response, sw.toString());
	}
	public void destroy() {
	}
	public final void init(FilterConfig config) throws ServletException {
		_ctx = config.getServletContext();

		final String ext = config.getInitParameter("extension");
		if (ext != null && ext.length() > 0)
			_ext = ext;

		final String cs = config.getInitParameter("charset");
		if (cs != null)
			_charset = cs.length() > 0 ? cs: null;
	}
}
