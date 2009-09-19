/* ExtendletDspContext.java

	Purpose:
		
	Description:
		
	History:
		Wed Oct 22 09:16:24     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp;

import java.io.Writer;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.web.util.resource.ExtendletContext;
import org.zkoss.web.servlet.BufferedResponse;

/**
 * Extendlet-based DSP context.
 *
 * @author tomyeh
 * @since 3.5.2
 */
public class ExtendletDspContext extends ServletDspContext {
	private final ExtendletContext _webctx;
	private final String _dir;

	public ExtendletDspContext(ExtendletContext webctx,
	HttpServletRequest request, HttpServletResponse response,
	String path, Writer out) {
		super(webctx.getServletContext(), request, response, out, webctx.getLocator());
		_webctx = webctx;

		if (path != null) {
			int j = path.lastIndexOf('/');
			_dir = j > 0 ? "~." + path.substring(0, j + 1): null;
		} else {
			_dir = null;
		}
	}

	public String encodeURL(String uri)
	throws ServletException, IOException {
		return _webctx.encodeURL(_request, _response, uri);
	}
	public void include(String uri, Map params)
	throws ServletException, IOException {
		if (_dir != null && uri != null && uri.length() > 0) {
			char cc = uri.charAt(0);
			if (cc != '~' && cc != '/')
				uri = _dir + uri;
		}
		_webctx.include(_request,
			(HttpServletResponse)BufferedResponse.getInstance(_response, _out),
			uri, params);
	}
}
