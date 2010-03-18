/* JspFns.java

	Purpose:
		
	Description:
		
	History:
		Fri Oct 17 08:57:02     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.fn;

import java.util.Iterator;
import java.io.StringWriter;
import java.util.Calendar;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.lang.Library;
import org.zkoss.util.logging.Log;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.HttpBufferedResponse;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.HtmlPageRenders;
import org.zkoss.zk.ui.http.WebManager;
import org.zkoss.zk.ui.http.ExecutionImpl;

/**
 * Utilities to generate ZK related information in JSP pages.
 *
 * <p>For DSP pages, use {@link DspFns} instead.<br/>
 * For ZUML pages, use {@link ZkFns} instead.
 *
 * @author tomyeh
 * @since 3.5.2
 */
public class JspFns {
	private static final Log log = Log.lookup(JspFns.class);
	private static long LAST_MODIFIED = new java.util.Date().getTime();

	/** Generates and returns the ZK specific HTML tags such as stylesheet
	 * and JavaScript.
	 * If you want to generate HTML HEAD and BODY tags by yourself in
	 * a non-ZUML page (e.g., JSP or DSP), you can invoke this method at
	 * the location you want (such as inside the HTML HEAD tag).
	 *
	 * @return the string holding the HTML tags, or null if already generated.
	 * @param deviceType the device type. If null, ajax is assumed.
	 */
 	public static String outZkHtmlTags(ServletContext ctx,
 	HttpServletRequest request, HttpServletResponse response,
 	String deviceType) {
 		Execution old = Executions.getCurrent();
 		Execution exec = new ExecutionImpl(ctx, request, response, null, null); 
		ExecutionsCtrl.setCurrent(exec);
 		((ExecutionCtrl)exec).onActivate();
		try {
			return HtmlPageRenders.outZkTags(exec,
				WebManager.getWebManager(ctx).getWebApp(),
				deviceType != null ? deviceType: "ajax");
		} finally {
			((ExecutionCtrl)exec).onDeactivate();
			ExecutionsCtrl.setCurrent(old);
		}
 	}
	/** Generates and returns the complete CSS content of all components in the
	 * specified device.
	 * <p>Notice that it generates the content, while
	 * {@link #outDeviceStyleSheets} generates the HTML tag that
	 * will include the content.
	 * @since 5.0.0
	 */
	public static String outDeviceCSSContent(ServletContext ctx,
 	HttpServletRequest request, HttpServletResponse response,
 	String deviceType) {
 		final StringWriter sw = new StringWriter();
 		for (Iterator it = LanguageDefinition.getByDeviceType(deviceType).iterator();
 		it.hasNext();) {
 			final LanguageDefinition langdef = (LanguageDefinition)it.next();
			for (Iterator it2 = langdef.getCSSURIs().iterator(); it2.hasNext();) {
				final String uri = (String)it2.next();
				try {
					Servlets.include(ctx, request,
						HttpBufferedResponse.getInstance(response, sw), uri, null, 0);
				} catch (Throwable ex) {
					log.realCauseBriefly("Unable to load "+uri, ex);
				}
			}
		}
		return sw.getBuffer().toString();
	}
	/** Returns HTML tags to include style sheets of the specified device
	 * for the current application (never null).
	 *
	 * <p>This method is used for JSP pages.
	 * @param deviceType the device type. If null, ajax is assumed.
	 */
	public static final String outDeviceStyleSheets(ServletContext ctx,
 	HttpServletRequest request, HttpServletResponse response,
 	String deviceType) {
 		Execution old = Executions.getCurrent();
 		Execution exec = new ExecutionImpl(ctx, request, response, null, null); 
		ExecutionsCtrl.setCurrent(exec);
 		((ExecutionCtrl)exec).onActivate();
		try {
			return HtmlPageRenders.outLangStyleSheets(exec,
				WebManager.getWebManager(ctx).getWebApp(),
				deviceType != null ? deviceType: "ajax");
		} finally {
			((ExecutionCtrl)exec).onDeactivate();
			ExecutionsCtrl.setCurrent(old);
		}
 	}

	/** Returns HTML tags to include JavaScript files of the specified
	 * device for the current application (never null).
	 * @since 5.0.0
	 */
	public static final String outDeviceJavaScripts(ServletContext ctx,
 	HttpServletRequest request, HttpServletResponse response,
 	String deviceType) {
 		Execution old = Executions.getCurrent();
 		Execution exec = new ExecutionImpl(ctx, request, response, null, null); 
		ExecutionsCtrl.setCurrent(exec);
 		((ExecutionCtrl)exec).onActivate();
		try {
			return HtmlPageRenders.outLangJavaScripts(exec,
				WebManager.getWebManager(ctx).getWebApp(),
				deviceType != null ? deviceType: "ajax");
		} finally {
			((ExecutionCtrl)exec).onDeactivate();
			ExecutionsCtrl.setCurrent(old);
		}
	}
	
	/** Sets the Cache-Control, Expires, and Last-Modified headers for the response.
	 * <p> Last-Modified is a "weak" caching header in that the browser applies
	 * a heuristic to determine whether to fetch the item from cache or not. Use
	 * {@link #setCacheControl(ServletContext, HttpServletRequest, HttpServletResponse, String, int)}
	 * instead.
	 * 
	 * @param response the servlet response (never null)
	 * @param prop the name of the propery to check if the headers
	 * shall be generated. If null, it is always generated.
	 * If "false" is specified with this property, this method won't
	 * generate anything. In other words, "false" means to disable the cache.
	 * If It is used for debugging/developing purpose.
	 * @param hours the number of hours the client is allowed to cache the
	 * resource
	 * @since 3.6.3
	 * @see #setCacheControl(ServletContext, HttpServletRequest, HttpServletResponse, String, int)
	 */
	public static void setCacheControl(HttpServletResponse response,
	String prop, int hours) {
		if (prop == null || !"false".equals(Library.getProperty(prop))) {	
			response.setHeader("Cache-Control", "public, max-age="
				+ hours * 3600); //unit: seconds

			final Calendar cal = Calendar.getInstance();
			cal.add(cal.HOUR, hours);
			response.setDateHeader("Expires", cal.getTime().getTime());
			response.setDateHeader("Last-Modified", LAST_MODIFIED);
		}
	}

	
	/** Sets the Cache-Control, Expires, and Etag headers for the response.
	 * 
	 * @param context the servlet context (never null)
	 * @param request the servlet request (never null)
	 * @param response the servlet response (never null)
	 * @param prop the name of the propery to check if the headers
	 * shall be generated. If null, it is always generated.
	 * If "false" is specified with this property, this method won't
	 * generate anything. In other words, "false" means to disable the cache.
	 * If It is used for debugging/developing purpose.
	 * @param hours the number of hours the client is allowed to cache the
	 * resource
	 * @return whether HttpServletResponse.SC_NOT_MODIFIED is set.
	 * @since 5.0.1
	 */
	public static boolean setCacheControl(ServletContext context, HttpServletRequest request,
			HttpServletResponse response, String prop, int hours) {
		if (prop == null || !"false".equals(Library.getProperty(prop))) {	
			response.setHeader("Cache-Control", "public, max-age="
				+ hours * 3600); //unit: seconds

			final Calendar cal = Calendar.getInstance();
			cal.add(cal.HOUR, hours);
			response.setDateHeader("Expires", cal.getTime().getTime());
			response.setDateHeader("Last-Modified", LAST_MODIFIED);
			if (shallETag()) {
				final String etag = WebManager.getWebManager(context).getClassWebResource().getEncodeURLPrefix();
				final String inm = request.getHeader("If-None-Match");
				if (inm != null && inm.equals(etag)) {
					response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
					response.setHeader("ETag", etag);
					request.setAttribute("ETagMatched", Boolean.TRUE);
					return true;
				}
				response.setHeader("ETag", etag);
			}
		}
		return false;
	}
	private static final boolean shallETag() {
		if (_shallETag == null) {
			String s = Library.getProperty("org.zkoss.web.classWebResource.cache.etag");
			_shallETag = Boolean.valueOf("true".equals(s));
		}
		return _shallETag.booleanValue();
	}
	private static Boolean _shallETag;

	/** Sets the Cache-Control, Expires, and Last-Modified headers for the CSS files
	 * of class Web resources.
	 * <p> Last-Modified is a "weak" caching header in that the browser applies
	 * a heuristic to determine whether to fetch the item from cache or not. Use
	 * {@link JspFns#setCSSCacheControl(ServletContext, HttpServletRequest, HttpServletResponse)}
	 * instead.
	 *
	 * <p>It first check if <tt>org.zkoss.web.classWebResource.cache</tt>
	 * is turned off, and then check how many hours specified in
	 * <tt>org.zkoss.web.classWebResource.cache.CSS.hours</tt>.
	 * If it is turned off or the value of hours is non-postive, nothing is generated
	 * Otherwise, it generates the header with the specified hours
	 * (default: 8760).
	 * @see #setCWRCacheControl
	 * @see #setCSSCacheControl(ServletContext, HttpServletRequest, HttpServletResponse)
	 * @since 3.6.3
	 */
	public static void setCSSCacheControl(HttpServletResponse response) {
		setCSSCacheControl(null, null, response);
	}
	/** Sets the Cache-Control, Expires, and Etag headers for the CSS files
	 * of class Web resources.
	 *
	 * <p>It first check if <tt>org.zkoss.web.classWebResource.cache</tt>
	 * is turned off, and then check how many hours specified in
	 * <tt>org.zkoss.web.classWebResource.cache.CSS.hours</tt>.
	 * If it is turned off or the value of hours is non-postive, nothing is generated
	 * Otherwise, it generates the header with the specified hours
	 * (default: 8760).
	 * @return whether HttpServletResponse.SC_NOT_MODIFIED is set.
	 * @see #setCWRCacheControl(ServletContext, HttpServletRequest, HttpServletResponse)
	 * @since 5.0.1
	 */
	public static boolean setCSSCacheControl(ServletContext context,
			HttpServletRequest request, HttpServletResponse response) {
		int hours = 8760;
		final String PROP = "org.zkoss.web.classWebResource.cache.CSS.hours";
		String s = Library.getProperty(PROP);
		if (s != null)
			try {
				hours = Integer.parseInt(s);
				if (hours <= 0) return false;
			} catch (Throwable ex) {
				log.warning("Ingored property "+PROP+": an integer is expected");
			}
		if (context != null)
			return setCacheControl(context, request, response, "org.zkoss.web.classWebResource.cache", hours);

		setCacheControl(response, "org.zkoss.web.classWebResource.cache", hours);
		return false;
	}
	/** Sets the Cache-Control, Expires, and Last-Modified headers for class Web resources.
	 * <p> Last-Modified is a "weak" caching header in that the browser applies
	 * a heuristic to determine whether to fetch the item from cache or not. Use
	 * {@link JspFns#setCWRCacheControl(ServletContext, HttpServletRequest, HttpServletResponse)}
	 * instead.
	 * It checks if <tt>org.zkoss.web.classWebResource.cache</tt>
	 * is turned off. If not, it generates the headers.
	 * <p>Notice that, for the CSS files, please use {@link #setCSSCacheControl}
	 * instead.
	 * @since 3.6.3
	 */
	public static void setCWRCacheControl(HttpServletResponse response) {
		setCacheControl(response, "org.zkoss.web.classWebResource.cache", 8760);
	}
	/** Sets the Cache-Control, Expires, and Etag headers for class Web resources.
	 * It checks if <tt>org.zkoss.web.classWebResource.cache</tt>
	 * is turned off. If not, it generates the headers.
	 * <p>Notice that, for the CSS files, please use {@link #setCSSCacheControl}
	 * instead.
	 * @return whether HttpServletResponse.SC_NOT_MODIFIED is set.
	 * @since 5.0.1
	 */
	public static boolean setCWRCacheControl(ServletContext context,
			HttpServletRequest request, HttpServletResponse response) {
		return setCacheControl(context, request, response, "org.zkoss.web.classWebResource.cache", 8760);
	}
}
