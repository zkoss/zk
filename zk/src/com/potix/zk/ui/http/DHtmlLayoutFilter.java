/* DHtmlLayoutFilter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Nov 30 00:49:59     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.http;

import java.io.StringWriter;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.potix.util.logging.Log;
import com.potix.web.servlet.GenericFilter;
import com.potix.web.servlet.BufferedResponse;

import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.Session;
import com.potix.zk.ui.Execution;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.metainfo.PageDefinition;
import com.potix.zk.ui.sys.UiFactory;
import com.potix.zk.ui.sys.WebAppCtrl;
import com.potix.zk.ui.sys.SessionsCtrl;
import com.potix.zk.ui.sys.RequestInfo;
import com.potix.zk.ui.impl.RequestInfoImpl;

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
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class DHtmlLayoutFilter extends GenericFilter {
	private static final Log log = Log.lookup(DHtmlLayoutFilter.class);

	private String _ext = "html";

	public void init() throws ServletException {
		super.init();

		String ext = getInitParameter("extension");
		if (ext != null && ext.length() > 0) _ext = ext;
	}

	private void process(HttpServletRequest request,
	HttpServletResponse response, String content)
	throws ServletException, IOException {
		if (log.debugable()) log.debug("Content to filter:\n"+content);

		final ServletContext ctx = getServletContext();
		final WebManager webman = WebManager.getWebManager(ctx);
		final WebApp wapp = webman.getWebApp();
		final WebAppCtrl wappc = (WebAppCtrl)wapp;
		final Session sess = webman.getSession(ctx, request);
		SessionsCtrl.setCurrent(sess);
		try {
			final Desktop desktop = webman.getDesktop(sess, request, null);
			final RequestInfo ri = new RequestInfoImpl(
				wapp, sess, desktop, request, wapp);
			final UiFactory uf = wappc.getUiFactory();
			final PageDefinition pagedef =
				uf.getPageDefinitionDirectly(ri, content, _ext);
			final Page page = uf.newPage(ri, pagedef, null);
			final Execution exec =
				new ExecutionImpl(ctx, request, response, desktop, page);
			wappc.getUiEngine()
				.execNewPage(exec, pagedef, page, response.getWriter());
		} finally {
			SessionsCtrl.setCurrent(null);
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
}
