/** RichletFilter.java.

	Purpose:
		
	Description:
		
	History:
		2:22:20 PM Aug 5, 2013, Created by jumperchen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zk.ui.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.impl.RequestInfoImpl;
import org.zkoss.zk.ui.metainfo.PageDefinitions;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.util.DesktopRecycle;

/**
 * A filter used for ZK Richlet
 * <p>To enable this filter, you can specify the following statement into web.xml and zk.xml.
 * For example,
 * <pre><code>
&lt;filter&gt;
	&lt;filter-name&gt;RichletFilter&lt;/filter-name&gt;
	&lt;filter-class&gt;org.zkoss.zk.ui.http.RichletFilter&lt;/filter-class&gt;
&lt;/filter&gt;

&lt;filter-mapping&gt;
	&lt;filter-name&gt;RichletFilter&lt;/filter-name&gt;
	&lt;url-pattern&gt;/zk/*&lt;/url-pattern&gt;
&lt;/filter-mapping&gt;
 * </code></pre>
 *
 * In the zk.xml
 * 
 * <pre><code>
&lt;richlet&gt;
	&lt;richlet-name&gt;Foo&lt;/richlet-name&gt;
	&lt;richlet-class&gt;foo.FooRichlet&lt;/richlet-class&gt;
	&lt;init-param&gt;
		&lt;param-name&gt;any&lt;/param-name&gt;
		&lt;param-value&gt;any&lt;/param-value&gt;
	&lt;/init-param&gt;
&lt;/richlet&gt;
&lt;richlet-mapping&gt;
	&lt;richlet-name&gt;Foo&lt;/richlet-name&gt;
	&lt;url-pattern&gt;/zk/foo/*&lt;/url-pattern&gt;
&lt;/richlet-mapping&gt;
 * </code></pre>
 * 
 * <strong>Note:</strong> The url-pattern of richlet-mapping should start with
 * <tt>/zk</tt> in this example which is the filter-mapping mapping to.
 * 
 * <p>Init parameters:
 *
 * <dl>
 * <dt>compress</dt>
 * <dd>It specifies whether to compress the output if the browser supports the compression (Accept-Encoding)
 * and this Filter is not included by other Filters.</dd>
 * </dl>
 * @author jumperchen
 * @since 7.0.0
 */
public class RichletFilter implements Filter {
	private static final Logger log = LoggerFactory.getLogger(RichletFilter.class);
	private FilterConfig _config;
	private WebManager _webman;
	private boolean _compress = true;

	public void init(FilterConfig config) throws ServletException {
		_config = config;
		String param = config.getInitParameter("compress");
		_compress = param == null || param.length() == 0 || "true".equals(param);
		final ServletContext ctx = _config.getServletContext();
		_webman = WebManager.getWebManagerIfAny(ctx);
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			HttpServletRequest reg = (HttpServletRequest) request;
			HttpServletResponse resp = (HttpServletResponse) response;
			final Session sess = WebManager.getSession(_config.getServletContext(), (HttpServletRequest) request);
			String path = Https.getThisPathInfo(request);
			if (path == null) {
				path = Https.getThisServletPath(request);
			}

			try {
				final Object old = I18Ns.setup(sess, request, response,
						sess.getWebApp().getConfiguration().getResponseCharset());
				try {
					if (process(sess, reg, resp, path, true))
						return; // done
				} catch (Throwable ex) {
					// pass
				} finally {
					I18Ns.cleanup(request, old);
				}
			} finally {
				SessionsCtrl.requestExit(sess);
			}
		}

		chain.doFilter(request, response);
	}

	protected boolean process(Session sess, HttpServletRequest request, HttpServletResponse response, String path,
			boolean bRichlet) throws ServletException, IOException {
		final WebApp wapp = sess.getWebApp();
		final WebAppCtrl wappc = (WebAppCtrl) wapp;
		final Configuration config = wapp.getConfiguration();

		final boolean bInclude = Servlets.isIncluded(request);
		final boolean compress = _compress && !bInclude;
		final Writer out = compress ? (Writer) new StringWriter() : response.getWriter();
		final DesktopRecycle dtrc = bInclude ? null : config.getDesktopRecycle();
		final ServletContext ctx = _config.getServletContext();
		Desktop desktop = dtrc != null ? DesktopRecycles.beforeService(dtrc, ctx, sess, request, response, path) : null;

		try {
			if (desktop != null) { // recycle
				final Page page = Utils.getMainPage(desktop);
				if (page != null) {
					final Execution exec = new ExecutionImpl(ctx, request, response, desktop, page);
					WebManager.setDesktop(request, desktop);
					wappc.getUiEngine().recycleDesktop(exec, page, out);
				} else
					desktop = null; // something wrong (not possible; just in
									// case)
			}
			if (desktop == null) {
				desktop = _webman.getDesktop(sess, request, response, path, true);
				if (desktop == null) // forward or redirect
					return true;

				final RequestInfo ri = new RequestInfoImpl(wapp, sess, desktop, request,
						PageDefinitions.getLocator(wapp, path));
				((SessionCtrl) sess).notifyClientRequest(true);

				final UiFactory uf = wappc.getUiFactory();
				if (uf.isRichlet(ri, bRichlet)) {
					final Richlet richlet = uf.getRichlet(ri, path);
					if (richlet == null)
						return false; // not found

					final Page page = WebManager.newPage(uf, ri, richlet, response, path);
					final Execution exec = new ExecutionImpl(ctx, request, response, desktop, page);
					wappc.getUiEngine().execNewPage(exec, richlet, page, out);
					// no need to set device type here, since UiEngine will do
					// it later
				} else {
					return false; // not found
				}
			}

			if (compress) {
				final String result = ((StringWriter) out).toString();

				try {
					final OutputStream os = response.getOutputStream();
					// Call it first to ensure getWrite() is not called yet

					byte[] data = result.getBytes(config.getResponseCharset());
					if (data.length > 200) {
						byte[] bs = Https.gzip(request, response, null, data);
						if (bs != null)
							data = bs; // yes, browser support compress
					}

					response.setContentLength(data.length);
					os.write(data);
					response.flushBuffer();
				} catch (IllegalStateException ex) { // getWriter is called
					response.getWriter().write(result);
				}
			}
		} finally {
			if (dtrc != null)
				DesktopRecycles.afterService(dtrc, desktop);
		}
		return true; // success
	}

	public void destroy() {
		_config = null;
		_webman = null;
	}

}
