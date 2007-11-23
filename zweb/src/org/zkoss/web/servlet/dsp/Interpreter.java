/* Interpreter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep  5 11:12:47     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp;

import java.io.Writer;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.zkoss.xel.FunctionMapper;

import org.zkoss.lang.D;
import org.zkoss.util.media.ContentTypes;
import org.zkoss.util.resource.Locator;
import org.zkoss.util.logging.Log;

import org.zkoss.web.servlet.dsp.impl.Parser;

/**
 * The interpreter of the DSP file.
 *
 * <p>Note: we recognize only <%, <\%, %>, %\>, ${ and $\{.
 * Unlike JSP, we don't recognize \${ or \$\{.
 *
 * @author tomyeh
 */
public class Interpreter {
//	private static final Log log = Log.lookup(Interpreter.class);

	/** Returns the content type by specifying a path, or null
	 * if no content type is available or path is null.
	 *
	 * <p>It determines the content type by looking the extension.
	 * Note: it considers the extension of "a.css.dsp" as "css".
	 */
	public static final String getContentType(String path) {
		if (path == null) return null;

		int j = path.lastIndexOf('.');
		if (j < 0 || path.indexOf('/', j + 1) >= 0) return null;

		int k = path.indexOf(';', j + 1); //it might contain session-id
		String ext =
			(k >= 0 ? path.substring(j + 1, k): path.substring(j + 1))
			.toLowerCase();
		if ("dsp".equals(ext)) {
			if (j == 0) return null; //unknown
			k = path.lastIndexOf('.', j - 1);
			if (k < 0) return null; //unknown
			ext = path.substring(k + 1, j);
			if (ext.indexOf('/') >= 0) return null; //unknown
		}
		return ContentTypes.getContentType(ext);
	}
	/** Constructor.
	 */
	public Interpreter() {
	}

	/** Parses a content to a meta format called {@link Interpretation}.
	 * @param ctype the content type. Optional. It is used only if
	 * no page action at all. If it is not specified and not page
	 * action, "text/html" is assumed.
	 */
	public final Interpretation parse(String content, String ctype,
	FunctionMapper fm, Locator loc)
	throws javax.servlet.ServletException, IOException {
		return new Parser().parse(content, ctype, fm, loc);
	}
	/** Interprets the specified content and generates the result to
	 * the output specified in {@link DSPContext}.
	 *
	 * @param dc the interpreter context; never null.
	 * @param content the content of DSP to interpret
	 * @param ctype the content type. Optional. It is used only if
	 * no page action at all. If it is not specified and not page
	 * action, "text/html" is assumed.
	 */
	public final void interpret(DSPContext dc, FunctionMapper fm,
	String content, String ctype)
	throws javax.servlet.ServletException, IOException {
		parse(content, ctype, fm, dc.getLocator()).interpret(dc);
	}
	/** Interprets the specified content based on the HTTP request.
	 * It actually wraps the HTTP request into {@link DSPContext}
	 * and then invoke {@link #interpret(DSPContext, FunctionMapper, String, String)}.
	 *
	 * @param locator used to locate resources, such as taglib.
	 * If null is specified, the locator for the specified servlet context is
	 * used. (In other words, we use {@link org.zkoss.web.util.resource.ServletContextLocator}
	 * if locator is null).
	 * @param ctype the content type. Optional. It is used only if
	 * no page action at all. If it is not specified and not page
	 * action, "text/html" is assumed.
	 */
	public final void interpret(ServletContext ctx,
	HttpServletRequest request, HttpServletResponse response,
	String content, String ctype, Locator locator)
	throws javax.servlet.ServletException, IOException {
		interpret(
			new ServletDSPContext(ctx, request, response, locator),
			null, content, ctype);
	}
	/** Interprets the specified content based on the HTTP request.
	 * It actually wraps the HTTP request into {@link DSPContext}
	 * and then invoke {@link #interpret(DSPContext, FunctionMapper, String, String)}.
	 *
	 * @param locator used to locate resources, such as taglib.
	 * If null is specified, the locator for the specified servlet context is
	 * used. (In other words, we use {@link org.zkoss.web.util.resource.ServletContextLocator}
	 * if locator is null).
	 * @param ctype the content type. Optional. It is used only if
	 * no page action at all. If it is not specified and not page
	 * action, "text/html" is assumed.
	 * @param out the output to generate the result.
	 * If null, it is the same as {@link #interpret(ServletContext,HttpServletRequest,HttpServletResponse,String,String,Locator)}
	 * In other words, response.getWriter() is used.
	 * @since 2.4.1
	 */
	public final void interpret(ServletContext ctx,
	HttpServletRequest request, HttpServletResponse response, Writer out,
	String content, String ctype, Locator locator)
	throws javax.servlet.ServletException, IOException {
		interpret(
			new ServletDSPContext(ctx, request, response, out, locator),
			null, content, ctype);
	}
}
