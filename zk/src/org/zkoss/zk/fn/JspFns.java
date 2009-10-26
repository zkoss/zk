/* JspFns.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 17 08:57:02     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.fn;

import java.util.Calendar;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.lang.Library;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
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
			return ZkFns.outZkHtmlTags(exec,
				WebManager.getWebManager(ctx).getWebApp(),
				deviceType != null ? deviceType: "ajax");
		} finally {
			((ExecutionCtrl)exec).onDeactivate();
			ExecutionsCtrl.setCurrent(old);
		}
 	}
	/** Returns HTML tags to include style sheets of the specified device
	 * of the current application (never null).
	 *
	 * <p>It is the same as {@link DspFns#outDeviceStyleSheets}
	 * except this method is used for JSP pages.
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
			return ZkFns.outDeviceStyleSheets(exec,
				WebManager.getWebManager(ctx).getWebApp(),
				deviceType != null ? deviceType: "ajax");
		} finally {
			((ExecutionCtrl)exec).onDeactivate();
			ExecutionsCtrl.setCurrent(old);
		}
 	}


	/** Sets the Cache-Control and Expires headers for the response.
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
	 */
	public static void setCacheControl(HttpServletResponse response,
	String prop, int hours) {
		if (prop == null || !"false".equals(Library.getProperty(prop))) {	
			response.setHeader("Cache-Control", "public, max-age="
				+ hours * 3600); //unit: seconds

			final Calendar cal = Calendar.getInstance();
			cal.add(cal.HOUR, hours);
			response.setDateHeader("Expires", cal.getTime().getTime());
		}
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
	public static void setCSSCacheControl(HttpServletResponse response) {
		int hours = 8760;
		final String PROP = "org.zkoss.web.classWebResource.cache.CSS.hours";
		String s = Library.getProperty(PROP);
		if (s != null)
			try {
				hours = Integer.parseInt(s);
				if (hours <= 0) return;
			} catch (Throwable ex) {
				log.warning("Ingored property "+PROP+": an integer is expected");
			}
		setCacheControl(response, "org.zkoss.web.classWebResource.cache", hours);
	}
	/** Sets the Cache-Control and Expires headers for class Web resources.
	 * It checks if <tt>org.zkoss.web.classWebResource.cache</tt>
	 * is turned off. If not, it generates the headers.
	 * <p>Notice that, for the CSS files, please use {@link #setCSSCacheControl}
	 * instead.
	 * @since 3.6.3
	 */
	public static void setCWRCacheControl(HttpServletResponse response) {
		setCacheControl(response, "org.zkoss.web.classWebResource.cache", 8760);
	}
}
