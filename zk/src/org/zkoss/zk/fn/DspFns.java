/* DspFns.java

	Purpose:
		
	Description:
		
	History:
		Fri Oct 17 09:31:58     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.fn;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.web.fn.ServletFns;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.sys.HtmlPageRenders;

/**
 * Utilities to generate ZK related information in DSP pages.
 *
 * <p>For JSP pages, use {@link JspFns} instead.<br/>
 * For ZUML pages, use {@link ZkFns} instead.
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public class DspFns {
	/** Generates and returns the ZK specific HTML tags such as stylesheet
	 * and JavaScript.
	 * If you want to generate HTML HEAD and BODY tags by yourself in
	 * a non-ZUML page (DSP), you can invoke this method at
	 * the location you want (such as inside the HTML HEAD tag).
	 *
	 * @return the string holding the HTML tags, or null if already generated.
	 * @param deviceType the device type. If null, ajax is assumed.
	 */
 	public static String outZkHtmlTags(String deviceType) {
 		Execution exec = Executions.getCurrent();
 		if (exec != null)
	 		return HtmlPageRenders.outZkTags(exec, null, null);

	 	return JspFns.outZkHtmlTags(
			ServletFns.getCurrentServletContext(),
			(HttpServletRequest)ServletFns.getCurrentRequest(),
			(HttpServletResponse)ServletFns.getCurrentResponse(),
			deviceType);
 	}

	/** Generates the complete CSS content of all components in the
	 * specified device.
	 * <p>Notice that it generates the content, while
	 * {@link #outDeviceStyleSheets} generates the HTML tag that
	 * will include the content.
	 * @since 5.0.0
	 */
	public static String outDeviceCSSContent(String deviceType) {
	 	return JspFns.outDeviceCSSContent(
			ServletFns.getCurrentServletContext(),
			(HttpServletRequest)ServletFns.getCurrentRequest(),
			(HttpServletResponse)ServletFns.getCurrentResponse(),
			deviceType);
	}
	/** Returns HTML tags to include style sheets of the specified device
	 * for the current application (never null).
	 *
	 * <p>This method is used for DSP pages.
	 * @param deviceType the device type. If null, ajax is assumed.
	 */
	public static final String outDeviceStyleSheets(String deviceType) {
		final Execution exec = Executions.getCurrent();
 		if (exec != null)
			return HtmlPageRenders.outLangStyleSheets(exec, null, null);

	 	return JspFns.outDeviceStyleSheets(
			ServletFns.getCurrentServletContext(),
			(HttpServletRequest)ServletFns.getCurrentRequest(),
			(HttpServletResponse)ServletFns.getCurrentResponse(),
			deviceType);
	}

	/** Returns HTML tags to include JavaScript files of the specified
	 * device for the current application (never null).
	 * @since 5.0.0
	 */
	public static final String outDeviceJavaScripts(String deviceType) {
		final Execution exec = Executions.getCurrent();
 		if (exec != null)
			return HtmlPageRenders.outLangJavaScripts(exec, null, null);

	 	return JspFns.outDeviceJavaScripts(
			ServletFns.getCurrentServletContext(),
			(HttpServletRequest)ServletFns.getCurrentRequest(),
			(HttpServletResponse)ServletFns.getCurrentResponse(),
			deviceType);
	}

	/** Sets the Cache-Control and Expires headers for the response.
	 *
	 * @param prop the name of the propery to check if the headers
	 * shall be generated. If null, it is always generated.
	 * If "false" is specified with this property, this method won't
	 * generate anything. In other words, "false" means to disable the cache.
	 * If It is used for debugging/developing purpose.
	 * @param hours the number of hours the client is allowed to cache the
	 * resource
	 * @since 3.6.3
	 */
	public static void setCacheControl(String prop, int hours) {
		JspFns.setCacheControl((HttpServletResponse)ServletFns.getCurrentResponse(),
			prop, hours);
	}
	/** Sets the Cache-Control and Expires headers for the CSS files
	 * of class Web resources.
	 *
	 * <p>It first check if <tt>org.zkoss.web.classWebResource.cache</tt>
	 * is turned off, and then check how many hours specified in
	 * <tt>org.zkoss.web.classWebResource.cache.CSS.hours</tt>.
	 * If it is turned off or the value of hours is non-postive, nothing is generated
	 * Otherwise, it generates the header with the specified hours
	 * (default: 8760).
	 * @see #setCWRCacheControl
	 * @since 3.6.3
	 */
	public static void setCSSCacheControl() {
		JspFns.setCSSCacheControl((HttpServletResponse)ServletFns.getCurrentResponse());
	}
	/** Sets the Cache-Control and Expires headers for class Web resources.
	 * It checks if <tt>org.zkoss.web.classWebResource.cache</tt>
	 * is turned off. If not, it generates the headers.
	 * <p>Notice that, for the CSS files, please use {@link #setCSSCacheControl}
	 * instead.
	 * @since 3.6.3
	 */
	public static void setCWRCacheControl() {
		JspFns.setCWRCacheControl((HttpServletResponse)ServletFns.getCurrentResponse());
	}
}
