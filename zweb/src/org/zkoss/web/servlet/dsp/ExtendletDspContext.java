/* ExtendletDspContext.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 22 09:16:24     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
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

	public ExtendletDspContext(ExtendletContext webctx,
	HttpServletRequest request, HttpServletResponse response,
	Writer out) {
		super(webctx.getServletContext(), request, response, out, webctx.getLocator());
		_webctx = webctx;
	}

	public String encodeURL(String uri)
	throws ServletException, IOException {
		return _webctx.encodeURL(_request, _response, uri);
	}
	public void include(String uri, Map params)
	throws ServletException, IOException {
		_webctx.include(_request,
			(HttpServletResponse)BufferedResponse.getInstance(_response, _out),
			uri, params);
	}
}
