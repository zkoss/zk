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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
}
