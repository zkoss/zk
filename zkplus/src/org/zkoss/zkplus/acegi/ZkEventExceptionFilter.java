/* ZkEventExceptionFilter.java

	Purpose:
		
	Description:
		
	History:
		Mon Dec 25 15:08:49     2006, Created by henrichen

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
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
 * Used to fire exception in the ZK's event processing queue (Used with MethodSecurityInterceptor).
 *
 * <p>How to handle the ZK's Ajax login support for Acegi Security System:</p>
 * <pre><code>
 *	&lt;bean id="zkFilterChainProxy" class="org.acegisecurity.util.FilterChainProxy">
 *		&lt;property name="filterInvocationDefinitionSource">
 *			&lt;value>
 *				CONVERT_URL_TO_LOWERCASE_BEFORE_COMPARISON
 *				PATTERN_TYPE_APACHE_ANT
 *				/zkau/**=zkAuthenticationProcessingFilter,zkExceptionTranslationFilter,zkEventExceptionFilter
 *			&lt;/value>
 *		&lt;/property>
 *	&lt;/bean>
 *
 *	&lt;bean id="filterChainProxy" class="org.acegisecurity.util.FilterChainProxy">
 *		&lt;property name="filterInvocationDefinitionSource">
 *			&lt;value>
 *				CONVERT_URL_TO_LOWERCASE_BEFORE_COMPARISON
 *				PATTERN_TYPE_APACHE_ANT
 *				/zkau/**=httpSessionContextIntegrationFilter,logoutFilter,rememberMeProcessingFilter,anonymousProcessingFilter
 *				/**=httpSessionContextIntegrationFilter,logoutFilter,authenticationProcessingFilter,securityContextHolderAwareRequestFilter,rememberMeProcessingFilter,anonymousProcessingFilter,exceptionTranslationFilter,filterInvocationInterceptor
 *			&lt;/value>
 *		&lt;/property>
 *	&lt;/bean>
 *
 *	&lt;bean id="zkEventExceptionFilter" class="org.zkoss.zkplus.acegi.ZkEventExceptionFilter"/>
 *
 *	&lt;bean id="zkExceptionTranslationFilter" class="org.acegisecurity.ui.ExceptionTranslationFilter">
 *		&lt;property name="authenticationEntryPoint">
 *			&lt;bean class="org.zkoss.zkplus.acegi.ZkAuthenticationEntryPoint">
 *				&lt;property name="loginFormUrl" value="~./acegilogin.zul"/>
 *				&lt;property name="forceHttps" value="false"/>
 *				&lt;property name="serverSideRedirect" value="true"/>
 *			&lt;/bean>
 *		&lt;/property>
 *		&lt;property name="accessDeniedHandler">
 *			&lt;bean class="org.zkoss.zkplus.acegi.ZkAccessDeniedHandler">
 *				&lt;property name="errorPage" value="~./accessDenied.zul"/>
 *			&lt;/bean>
 *		&lt;/property>
 *	&lt;/bean>
 *
 *	&lt;bean id="zkAuthenticationProcessingFilter" class="org.zkoss.zkplus.acegi.ZkAuthenticationProcessingFilter">
 *		&lt;property name="authenticationManager" ref="authenticationManager"/>
 *		&lt;property name="authenticationFailureUrl" value="~./acegilogin.zul?login_error=1"/>
 *		&lt;property name="defaultTargetUrl" value="/"/>
 *		&lt;property name="filterProcessesUrl" value="/j_acegi_security_check"/>
 *		&lt;property name="rememberMeServices" ref="zkRememberMeServices"/>
 *	&lt;/bean>
 *
 *	&lt;bean id="zkRememberMeServices" class="org.zkoss.zkplus.acegi.ZkTokenBasedRememberMeServices">
 *		&lt;property name="userDetailsService" ref="userDetailsService"/>
 *		&lt;property name="key" value="changeThis"/>
 *	&lt;/bean>
 *
 * ...
 * </code></pre>
 * <p>Applicable to Acegi Security version 1.0.3</p>
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
