/* DSPResourcelet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jul  4 15:57:24     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.util.resource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.lang.D;
import org.zkoss.lang.Exceptions;
import org.zkoss.io.Files;
import org.zkoss.util.logging.Log;
import org.zkoss.util.resource.ResourceCache;
import org.zkoss.util.resource.Loader;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.web.servlet.dsp.Interpreter;
import org.zkoss.web.servlet.dsp.Interpretation;
import org.zkoss.web.servlet.dsp.ServletDSPContext;

/**
 * The implementation of DSP resource processor ({@link Resourcelet}).
 *
 * @author tomyeh
 * @since 2.4.1
 */
public class DSPResourcelet implements Resourcelet {
	private static final Log log = Log.lookup(DSPResourcelet.class);

	private ExtendedWebContext _webctx;
	private ResourceCache _dspCache;

	public void init(ExtendedWebContext webctx) {
		_webctx = webctx;
		_dspCache = new ResourceCache(new DSPLoader(_webctx), 131);
		_dspCache.setMaxSize(1000).setLifetime(60*60*1000); //1hr
		_dspCache.setCheckPeriod(60*60*1000); //1hr
	}
	public void service(HttpServletRequest request,
	HttpServletResponse response, String pi, String extra)
	throws ServletException, IOException {
		final Interpretation cnt = (Interpretation)_dspCache.get(pi);
		if (cnt == null) {
			if (Servlets.isIncluded(request)) log.error("Failed to load the resource: "+pi);
				//It might be eaten, so log the error
			response.sendError(response.SC_NOT_FOUND, pi);
			return;
		}

		StringWriter sw =
			_webctx.shallCompress(request, get2ndExtension(pi)) ?
				new StringWriter(4096): null;
		cnt.interpret(new ServletDSPContext(
			_webctx.getServletContext(), request, response,
			sw, _webctx.getLocator()));
		if (extra != null)
			(sw != null ? (Writer)sw: response.getWriter()).write(extra);
		if (sw != null) {
			byte[] bs = sw.toString().getBytes("UTF-8");
			sw = null; //free
			byte[] data = Https.gzip(request, response, null, bs);
			if (data == null) //browser doesn't support compress
				data = bs;
			else
				bs = null; //free

			response.setContentLength(data.length);
			response.getOutputStream().write(data);
			response.flushBuffer();
		}
		return; //done
	}
	/** Returns the second extension. For example, js in xx.js.dsp.
	 */
	private static final String get2ndExtension(String pi) {
		int j = pi.lastIndexOf('.');
		if (j < 0 || pi.indexOf('/', j + 1) >= 0)
			return null;

		int k = j > 0 ? pi.lastIndexOf('.', j - 1): -1;
		if (k < 0 || pi.indexOf('/', k + 1) >= 0)
			return null;
		return pi.substring(k + 1, j).toLowerCase();
	}

	/** Helper class. */
	private static class DSPLoader implements Loader {
		private final ExtendedWebContext _webctx;
		private DSPLoader(ExtendedWebContext cwc) {
			_webctx = cwc;
		}

		//-- super --//
		public boolean shallCheck(Object src, long expiredMillis) {
			return expiredMillis > 0;
		}
		/** Returns the last modified time.
		 */
		public long getLastModified(Object src) {
			return 1; //any value (because it is packed in jar)
		}
		public Object load(Object src) throws Exception {
//			if (D.ON && log.debugable()) log.debug("Parse "+src);
			final String path = (String)src;
			final InputStream is = ClassWebResource.getResourceAsStream(path);
			if (is == null)
				return null;
			try {
				return parse0(is, Interpreter.getContentType(path));
			} catch (Exception ex) {
				if (log.debugable())
					log.realCauseBriefly("Failed to parse "+path, ex);
				else
					log.error("Failed to parse "+path
					+"\nCause: "+ex.getClass().getName()+" "+Exceptions.getMessage(ex)
					+"\n"+Exceptions.getBriefStackTrace(ex));
				return null; //as non-existent
			}
		}
		private Object parse0(InputStream is, String ctype) throws Exception {
			if (is == null) return null;

			final String content =
				Files.readAll(new InputStreamReader(is, "UTF-8"))
				.toString();
			return new Interpreter()
				.parse(content, ctype, null, _webctx.getLocator());
		}
	}
}
