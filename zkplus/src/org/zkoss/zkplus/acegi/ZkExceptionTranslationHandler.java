/* ZkExceptionTranslationHandler.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Dec  21 15:48:55     2006, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.acegi;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;

import org.zkoss.util.logging.Log;

import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.AcegiSecurityException;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.AuthenticationTrustResolver;
import org.acegisecurity.AuthenticationTrustResolverImpl;
import org.acegisecurity.InsufficientAuthenticationException;

import org.acegisecurity.context.SecurityContextHolder;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * <p>The ZK counter part of Acegi's ExceptionTranslationFilter.</p>
 * <p>Whenever an Acegi Exception occured inside Event handling, the Acegi's original servlet filter-based
 * mechanism will not work. The original mechanism will call Acegi's AuthenticationEntryPoint.commence() method
 * and redirect to the login url. That will "refresh" the whole browser page to a new page and
 * ruin ZK's original application page and all states gone. Resend original request is no way to construct the
 * original page since it is an Ajax request.</p>
 *
 * <p>This new ZkExceptionTranslationHandler is called by the ZK's {@link AcegiSecurityContextListener} if a Spring
 * bean "zkExceptionTranslationHandler" is defined. This ZkExceptionTranslationHandler will handle the Acegi's 
 * AuthenticationException and call ZkAuthenticationEtnryPoint.commence() to show a modal login window.</p>
 *
 * <p>You don't use this object directly, rather, you defined it as a Spring bean with id "zkExceptionTranslationHandler":</p>
 *
 * <pre><code>
 *	&lt;bean id="zkExceptionTranslationHandler" class="org.zkoss.zkplus.acegi.ZkExceptionTranslationHandler">
 *		&lt;property name="zkAuthenticationEntryPoint">
 *			&lt;bean class="org.zkoss.zkplus.acegi.SimpleAuthenticationEntryPoint">
 *				&lt;property name="loginFormUrl" value="~./acegilogin.zul"/>
 *			&lt;/bean>
 *		&lt;/property>
 *		&lt;property name="zkAccessDeniedHandler">
 *			&lt;bean class="org.zkoss.zkplus.acegi.SimpleAccessDeniedHandler">
 *				&lt;property name="errorPage" value="~./accessDenied.zul"/>
 *			&lt;/bean>
 *		&lt;/property>
 *	&lt;/bean>
 * </code></pre>
 *
 * @author henrichen
 */
public class ZkExceptionTranslationHandler implements InitializingBean {
    private static final Log log = Log.lookup(ZkExceptionTranslationHandler.class);
    
    private static final String OLD_EVENT = "org.zkoss.zkplus.acegi.OLD_EVENT";
    private static final String REASON = "org.zkoss.zkplus.acegi.REASON";
    private static final String ON_LOGIN = "onLogin_zkplus.acegi";

    private ZkAccessDeniedHandler _accessDeniedHandler = new SimpleAccessDeniedHandler();
    private ZkAuthenticationEntryPoint _authenticationEntryPoint;
    private AuthenticationTrustResolver _authenticationTrustResolver = new AuthenticationTrustResolverImpl();

	//-- InitialzingBean --//
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(_authenticationEntryPoint, "zkAuthenticationEntryPoint must be specified");
        Assert.notNull(_authenticationTrustResolver, "authenticationTrustResolver must be specified");
    }

	/**
	 * Called by the AcegiSecurityContextListener if a Spring bean named zkExceptionTranslationHanlder is defined.
	 */
    public void handle(Component comp, Event evt, AcegiSecurityException exception) {
        if (exception instanceof AuthenticationException) {
            if (log.debugable()) {
                log.debug("Authentication exception occurred; redirecting to authentication entry point.\n"
                	+ exception);
            }

            sendStartAuthentication(comp, evt, (AuthenticationException) exception);
        } else if (exception instanceof AccessDeniedException) {
            if (_authenticationTrustResolver.isAnonymous(SecurityContextHolder.getContext().getAuthentication())) {
                if (log.debugable()) {
                    log.debug("Access is denied (user is anonymous); redirecting to authentication entry point.\n" 
                    	+ exception);
                }

                sendStartAuthentication(comp, evt, 
                    new InsufficientAuthenticationException("Full authentication is required to access this resource"));
            } else { //access denied
                if (log.debugable()) {
                    log.debug("Access is denied (user is not anonymous); delegating to AccessDeniedHandler.\n" 
                    	+ exception);
                }
                _accessDeniedHandler.handle(comp, evt, (AccessDeniedException) exception);
            }
        }
    }

    protected void sendStartAuthentication(Component comp, Event evt, AuthenticationException reason) {
        // SEC-112: Clear the SecurityContextHolder's Authentication, as the
        // existing Authentication is no longer considered valid
        SecurityContextHolder.getContext().setAuthentication(null);

		if (!comp.isListenerAvailable(ON_LOGIN, true)) {
			comp.addEventListener(ON_LOGIN, new LoginEventListener());
		}

		comp.setAttribute(OLD_EVENT, evt);
		comp.setAttribute(REASON, reason);
		Events.postEvent(ON_LOGIN, comp, null);
    }
   
    public void setZkAccessDeniedHandler(ZkAccessDeniedHandler accessDeniedHandler) {
        _accessDeniedHandler = accessDeniedHandler;
    }

    public void setZkAuthenticationEntryPoint(ZkAuthenticationEntryPoint authenticationEntryPoint) {
        _authenticationEntryPoint = authenticationEntryPoint;
    }

    public void setAuthenticationTrustResolver(AuthenticationTrustResolver authenticationTrustResolver) {
        _authenticationTrustResolver = authenticationTrustResolver;
    }
    
	private class LoginEventListener implements EventListener {
		public boolean isAsap() {
			return true;
		}
		
		public void onEvent(Event event) {
			//fetch old Event stored in Session and post again
			final Component comp = event.getTarget();
			final Event evt = (Event) comp.getAttribute(OLD_EVENT);
			final AuthenticationException reason = (AuthenticationException) comp.getAttribute(REASON);

			//cleanup the attribute
			comp.removeAttribute(OLD_EVENT);
			comp.removeAttribute(REASON);
			
			if (_authenticationEntryPoint.commence(comp, event, reason)) {
				Events.postEvent(evt); //repost the old event after login
			}
		}
	}
}
