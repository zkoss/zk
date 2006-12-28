/* ZkEventExceptionFilter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Dec 25 15:08:49     2006, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.acegi;

import java.io.StringWriter;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.util.logging.Log;
import org.zkoss.web.servlet.BufferedResponse;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.impl.RequestInfoImpl;

import org.acegisecurity.AcegiSecurityException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Used to fire exception in the Filter-chain.
 *
 * <p>How to use:
 * <ul>
 * <li>Put after the Acegi Filter chain as the last one.</li>
 * </ul>
 *
 * @author henrichen
 */
public class ZkEventExceptionFilter implements Filter, InitializingBean {
	private static final Log log = Log.lookup(ZkEventExceptionFilter.class);
	/*package*/ static final String EXCEPTION = "org.zkoss.zkplus.acegi.EXCEPTION";
	/*package*/ static final String COMPONENT = "org.zkoss.zkplus.acegi.COMPONENT";
	/*package*/ static final String EVENT = "org.zkoss.zkplus.acegi.EVENT";

    public void afterPropertiesSet() throws Exception {}

	//-- Filter --//
	public void doFilter(ServletRequest request, ServletResponse response,
	FilterChain chain) throws IOException, ServletException {
		chain.doFilter(request, response);

		final AcegiSecurityException exception  = (AcegiSecurityException) request.getAttribute(EXCEPTION);

		if ( exception != null) {
			request.removeAttribute(EXCEPTION);
			throw exception;
		}
	}
	public void destroy() {}
	public final void init(FilterConfig config) throws ServletException {}
}
