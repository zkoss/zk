/* Portlets.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Apr  1 13:57:54     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.portlet;

import java.util.Map;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

import javax.servlet.ServletContext;
import javax.servlet.RequestDispatcher;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletContext;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;

import org.zkoss.lang.D;
import org.zkoss.lang.SystemException;
import org.zkoss.util.logging.Log;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.util.resource.ExtendletContext;

/**
 * Utilities to handle portlet.
 *
 * @author tomyeh
 */
public class Portlets {
	private static final Log log = Log.lookup(Portlets.class);

	/**
	 * Includes the resource at the specified URI.
	 * It enhances RequestDispatcher to allow the inclusion with
	 * a parameter map -- acutually converting parameters to a query string
	 * and appending it to uri.
	 *
	 * <p>NOTE: don't include query parameters in uri.
	 *
	 * @param uri the URI to include. It is OK to relevant (without leading
	 * '/'). If starts with "/", the context path of request is assumed.
	 * To reference to foreign context, use "~ctx/" where ctx is the
	 * context path of the foreign context (without leading '/').
	 * @param params the parameter map; null to ignore
	 * @param mode one of {@link #OVERWRITE_URI}, {@link #IGNORE_PARAM},
	 * and {@link #APPEND_PARAM}. It defines how to handle if both uri
	 * and params contains the same parameter.
	 */
	public static final
	void include(PortletContext ctx, RenderRequest request,
	RenderResponse response, String uri, Map params, int mode)
	throws IOException, PortletException {
		final PortletRequestDispatcher disp =
			getRequestDispatcher(ctx, uri, params, mode);
		if (disp == null)
			throw new PortletException("No dispatcher available to include "+uri);
		disp.include(request, response);
	}

	/** Returns the request dispatch of the specified URI.
	 *
	 * @param ctx the context used to resolve a foreign context.
	 * It is required only if uri starts with "~".
	 * @param uri the URI to include. It is OK to relevant (without leading
	 * '/'). If starts with "/", the context path of request is assumed.
	 * To reference to foreign context, use "~ctx/" where ctx is the
	 * context path of the foreign context (without leading '/').
	 * @param params the parameter map; null to ignore
	 * @param mode one of {@link #OVERWRITE_URI}, {@link #IGNORE_PARAM},
	 * and {@link #APPEND_PARAM}. It defines how to handle if both uri
	 * and params contains the same parameter.
	 */
	public static final PortletRequestDispatcher
	getRequestDispatcher(PortletContext ctx, String uri, Map params, int mode)
	throws PortletException {
		return new ParsedURI(ctx, uri).getRequestDispatcher(params, mode);
	}
	/** Returns the resource of the specified uri.
	 * Unlike PortletContext.getResource, it handles "~" like
	 * {@link #getRequestDispatcher} did.
	 */
	public static final URL getResource(PortletContext ctx, String uri)
	throws MalformedURLException {
		return new ParsedURI(ctx, uri).getResource();
	}
	/** Returns the resource stream of the specified uri.
	 * Unlike PortletContext.getResource, it handles "~" like
	 * {@link #getRequestDispatcher} did.
	 */
	public static final InputStream getResourceAsStream(
	PortletContext ctx, String uri) {
		return new ParsedURI(ctx, uri).getResourceAsStream();
	}
	/** Used to resolve "~" in URI. */
	private static class ParsedURI {
		/** The portlet context, null if _svlctx or _extctx is need. */
		private PortletContext _prtctx;
		/** External context if ~xxx/ is specified. */
		private ServletContext _svlctx;
		/** The extended context. */
		private ExtendletContext _extctx;
		private String _uri;

		private ParsedURI(final PortletContext ctx, final String uri) {
			if (uri != null && uri.startsWith("~")) { //refer to foreign context
				final int j = uri.indexOf('/', 1);
				final String ctxroot;
				if (j >= 0) {
					ctxroot = "/" + uri.substring(1, j);
					_uri = uri.substring(j);
				} else {
					ctxroot = "/" + uri.substring(1);
					_uri = "/";
				}

				final ServletContext svlctx = getServletContext(ctx);
				_extctx = Servlets.getExtendletContext(svlctx, ctxroot.substring(1));
				if (_extctx == null) {
					_svlctx = svlctx;
					_svlctx = _svlctx.getContext(ctxroot);
					if (_svlctx == null)
						throw new SystemException("Context not found or not visible to "+ctx+": "+ctxroot);
				}
			} else {
				_prtctx = ctx;
				_uri = uri;
			}
		}
		private PortletRequestDispatcher getRequestDispatcher(Map params, int mode) {
			if (_extctx == null && _svlctx == null && _prtctx == null) //not found
				return null;

			final String uri = generateURI(_uri, params, mode);
			if (_prtctx != null)
				return _prtctx.getRequestDispatcher(uri);

			final RequestDispatcher rd =
				_svlctx != null ? _svlctx.getRequestDispatcher(uri):
					_extctx.getRequestDispatcher(uri);
			return ServletPortletDispatcher.getInstance(rd);
		}
		private URL getResource() throws MalformedURLException {
			return _prtctx != null ? _prtctx.getResource(_uri):
				_svlctx != null ? _svlctx.getResource(_uri):
				_extctx != null ? _extctx.getResource(_uri): null;
		}
		private InputStream getResourceAsStream() {
			return _prtctx != null ? _prtctx.getResourceAsStream(_uri):
				_svlctx != null ? _svlctx.getResourceAsStream(_uri):
				_extctx != null ? _extctx.getResourceAsStream(_uri): null;
		}
	}

	private static final ServletContext getServletContext(PortletContext ctx) {
		return PortletServletContext.getInstance(ctx);
	}

	/** Whether to overwrite uri if both uri and params contain the same
	 * parameter.
	 * Used by {@link #generateURI}
	 */
	public static final int OVERWRITE_URI = Servlets.OVERWRITE_URI;
	/** Whether to ignore params if both uri and params contain the same
	 * parameter.
	 * Used by {@link #generateURI}
	 */
	public static final int IGNORE_PARAM = Servlets.IGNORE_PARAM;
	/** Whether to append params if both uri and params contain the same
	 * parameter. In other words, they both appear as the final query string.
	 * Used by {@link #generateURI}
	 */
	public static final int APPEND_PARAM = Servlets.APPEND_PARAM;
	/** Generates URI by appending the parameters.
	 * @param params the parameters to apend to the query string
	 * @param mode one of {@link #OVERWRITE_URI}, {@link #IGNORE_PARAM},
	 * and {@link #APPEND_PARAM}. It defines how to handle if both uri
	 * and params contains the same parameter.
	 * mode is used only if both uri contains query string and params is
	 * not empty.
	 */
	public static final String generateURI(String uri, Map params, int mode) {
		return Servlets.generateURI(uri, params, mode);
	}
}
