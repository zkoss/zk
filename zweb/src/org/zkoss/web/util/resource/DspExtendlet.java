/* DspExtendlet.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul  4 15:57:24     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.util.resource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.io.StringWriter;
import java.io.OutputStream;
import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.lang.D;
import org.zkoss.io.Files;
import org.zkoss.util.logging.Log;
import org.zkoss.util.media.ContentTypes;
import org.zkoss.util.resource.ResourceCache;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.web.servlet.dsp.Interpreter;
import org.zkoss.web.servlet.dsp.Interpretation;
import org.zkoss.web.servlet.dsp.ExtendletDspContext;

/**
 * The DSP resource processor ({@link Extendlet}) used to parse
 * DSP files loaded from the classpath.
 *
 * <p>Note: it assumes the file being loaded is UTF-8.
 * The encoding of the output stream is default to UTF-8, but
 * DSP can change by use of the page directive.
 *
 * @author tomyeh
 * @since 2.4.1 (public since 5.0.5)
 */
public class DspExtendlet implements Extendlet {
	private static final Log log = Log.lookup(DspExtendlet.class);

	private ExtendletContext _webctx;
	/** DSP Interpretation cache. */
	private ResourceCache<String, Interpretation> _cache;

	public void init(ExtendletConfig config) {
		_webctx = config.getExtendletContext();
		final DspLoader loader = new DspLoader();
		_cache = new ResourceCache<String, Interpretation>(loader, 131);
		_cache.setMaxSize(1024);
		_cache.setLifetime(60*60*1000); //1hr
		final int checkPeriod = loader.getCheckPeriod();
		_cache.setCheckPeriod(checkPeriod >= 0 ? checkPeriod: 60*60*1000); //1hr
	}
	public boolean getFeature(int feature) {
		return feature == ALLOW_DIRECT_INCLUDE;
	}
	public void service(HttpServletRequest request,
	HttpServletResponse response, String path)
	throws ServletException, IOException {
		final Interpretation cnt = _cache.get(path);
		if (cnt == null) {
			if (Servlets.isIncluded(request)) log.error("Failed to load the resource: "+path);
				//It might be eaten, so log the error
			response.sendError(response.SC_NOT_FOUND, path);
			return;
		}

		StringWriter sw =
			_webctx.shallCompress(request, get2ndExtension(path)) ?
				new StringWriter(4096): null;
		cnt.interpret(new ExtendletDspContext(_webctx, request, response, path, sw));

		if (sw != null) {
			final String result = sw.toString();
			sw = null; //free

			try {
				final OutputStream os = response.getOutputStream();
					//Call it first to ensure getWrite() is not called yet

				String charset = response.getCharacterEncoding();
				if (charset == null || charset.length() == 0)
					charset = "UTF-8";
				byte[] data = result.getBytes(charset);
				if (data.length > 200) {
					byte[] bs = Https.gzip(request, response, null, data);
					if (bs != null) data = bs; //yes, browser support compress
				}

				response.setContentLength(data.length);
				os.write(data);
			} catch (IllegalStateException ex) { //getWriter is called
				response.getWriter().write(result);
			}

			response.flushBuffer();
		}
	}
	/** Returns the second extension. For example, js in xx.js.dsp.
	 */
	private static final String get2ndExtension(String path) {
		int j = path.lastIndexOf('.');
		if (j < 0 || path.indexOf('/', j + 1) >= 0)
			return null;

		int k = j > 0 ? path.lastIndexOf('.', j - 1): -1;
		if (k < 0 || path.indexOf('/', k + 1) >= 0)
			return null;
		return path.substring(k + 1, j).toLowerCase();
	}

	/** Helper class. */
	private class DspLoader extends ExtendletLoader<Interpretation> {
		private DspLoader() {
		}

		//-- super --//
		protected Interpretation parse(InputStream is, String path, String orgpath)
		throws Exception {
			final String content =
				Files.readAll(new InputStreamReader(is, "UTF-8")).toString();

			String ctype = Interpreter.getContentType(path);
			if (ctype == null)
				ctype = ";charset=UTF-8";
			else if (ctype.indexOf(';') < 0 && !ContentTypes.isBinary(ctype))
				ctype += ";charset=UTF-8";
			return new Interpreter()
				.parse(content, ctype, null, _webctx.getLocator());
		}
		protected ExtendletContext getExtendletContext() {
			return _webctx;
		}
	}
}
