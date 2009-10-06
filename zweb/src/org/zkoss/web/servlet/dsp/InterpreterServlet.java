/* InterpreterServlet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep  5 17:06:34     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;

import org.zkoss.lang.D;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.SystemException;
import org.zkoss.util.logging.Log;
import org.zkoss.util.resource.ResourceCache;
import org.zkoss.util.resource.Locator;
import org.zkoss.io.Files;
import org.zkoss.xel.taglib.Taglibs;

import org.zkoss.web.servlet.Charsets;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.web.util.resource.ResourceCaches;
import org.zkoss.web.util.resource.ResourceLoader;
import org.zkoss.web.util.resource.ClassWebResource;

/**
 * The servlet used to interpret the DSP file (Potix Dynamic Script Page).
 *
 * <p>Initial parameters:
 * <dl>
 * <dt>charset</dt>
 * <dd>The default character set if not specified in the DSP page.<br/>
 * Default: UTF-8.</dd>
 * <dt>class-resource</dt>
 * <dd>Whether to search the class loader if a resource is not found
 * in the Web application (i.e., ServletContext).</dd>
 * Default: false.</dd>
 * </dl>
 *
 * @author tomyeh
 */
public class InterpreterServlet extends HttpServlet {
	private static final Log log = Log.lookup(InterpreterServlet.class);
	private ServletContext _ctx;
	private String _charset = "UTF-8";
	private Locator _locator;
	private boolean _compress = true;

	public void init(ServletConfig config) throws ServletException {
		//super.init(config);
			//Note callback super to avoid saving config

		_ctx = config.getServletContext();

		String param = config.getInitParameter("compress");
		if (param != null)
			_compress = "true".equals(param);

		param = config.getInitParameter("class-resource");
		final boolean bClsRes = "true".equals(param);
		_locator = new Locator() {
			public String getDirectory() {
				return null; //FUTURE: support relative path
			}
			public URL getResource(String name) {
				URL url = null;
				if (name.indexOf("://") < 0) {
					try {
						url = _ctx.getResource(name);
						if (bClsRes && url == null)
							url = ClassWebResource.getResource(name);
					} catch (java.net.MalformedURLException ex) { //eat it
					}
				}
				return url != null ? url: Taglibs.getDefaultURL(name);
			}
			public InputStream getResourceAsStream(String name) {
				InputStream is = _ctx.getResourceAsStream(name);
				return !bClsRes || is != null ? is:
					ClassWebResource.getResourceAsStream(name);
			}
		};

		param = config.getInitParameter("charset");
		if (param != null) _charset = param.length() > 0 ? param: null;
	}
	public ServletContext getServletContext() {
		return _ctx;
	}

	//-- super --//
	protected
	void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		final String path = Https.getThisServletPath(request);
		if (D.ON && log.debugable()) log.debug("Get "+path);

		final Object old = Charsets.setup(request, response, _charset);
		try {
			final Interpretation cnt = (Interpretation)
				ResourceCaches.get(getCache(), _ctx, path, null);
			if (cnt == null) {
				if (Https.isIncluded(request)) log.error("Not found: "+path);
					//It might be eaten, so log the error
				response.sendError(HttpServletResponse.SC_NOT_FOUND, path);
				return;
			}

			final boolean compress = _compress && !Servlets.isIncluded(request);
			final StringWriter out = compress ? new StringWriter(): null;
			cnt.interpret(
				new ServletDspContext(_ctx, request, response, out, null));

			if (compress) {
				final String result = out.toString();

				try {
					final OutputStream os = response.getOutputStream();
						//Call it first to ensure getWrite() is not called yet
	
					byte[] data = result.getBytes("UTF-8");
					if (data.length > 200) {
						byte[] bs = Https.gzip(request, response, null, data);
						if (bs != null) data = bs; //yes, browser support compress
					}
	
					response.setContentLength(data.length);
					os.write(data);
					response.flushBuffer();
				} catch (IllegalStateException ex) { //getWriter is called
					response.getWriter().write(result);
				}
			}
		} finally {
			Charsets.cleanup(request, old);
		}
	}
	protected
	void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		doGet(request, response);
	}

	private static final String ATTR_PAGE_CACHE = "org.zkoss.web.servlet.dsp.PageCache";
	private final ResourceCache getCache() {
		ResourceCache cache = (ResourceCache)_ctx.getAttribute(ATTR_PAGE_CACHE);
		if (cache == null) {
			synchronized (InterpreterServlet.class) {
				cache = (ResourceCache)_ctx.getAttribute(ATTR_PAGE_CACHE);
				if (cache == null) {
					cache = new ResourceCache(new MyLoader(), 29);
					cache.setMaxSize(1024);
					cache.setLifetime(60*60*1000); //1hr
					_ctx.setAttribute(ATTR_PAGE_CACHE, cache);
				}
			}
		}
		return cache;
	}
	private class MyLoader extends ResourceLoader {
		private MyLoader() {
		}
		//-- super --//
		protected Object parse(String path, File file, Object extra)
		throws Exception {
			final InputStream is = new BufferedInputStream(new FileInputStream(file));
			try {
				return parse0(is, Interpreter.getContentType(file.getName()));
			} catch (Exception ex) {
				if (log.debugable())
					log.realCauseBriefly("Failed to parse "+file, ex);
				else
					log.error("Failed to parse "+file+"\nCause: "+Exceptions.getMessage(ex)
						+"\n"+Exceptions.getBriefStackTrace(ex));
				return null; //as non-existent
			} finally {
				Files.close(is);
			}
		}
		protected Object parse(String path, URL url, Object extra)
		throws Exception {
			InputStream is = url.openStream();
			if (is != null) is = new BufferedInputStream(is);
			try {
				return parse0(is,
					Interpreter.getContentType(url.getPath()));
			} catch (Exception ex) {
				if (log.debugable())
					log.realCauseBriefly("Failed to parse "+url, ex);
				else
					log.error("Failed to parse "+url+"\nCause: "+Exceptions.getMessage(ex));
				return null; //as non-existent
			} finally {
				Files.close(is);
			}
		}
		private Object parse0(InputStream is, String ctype) throws Exception {
			if (is == null) return null;

			final String content =
				Files.readAll(new InputStreamReader(is, "UTF-8"))
				.toString();
			return new Interpreter().parse(content, ctype, null, _locator);
		}
	}
}
