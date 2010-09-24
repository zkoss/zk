/* AcegiSecurityContextListener.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep  11 12:55:11     2006, Created by henrichen

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.acegi;

import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.acegisecurity.AcegiSecurityException;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.zkoss.io.NullWriter;
import org.zkoss.lang.Exceptions;
import org.zkoss.util.logging.Log;
import org.zkoss.web.servlet.BufferedResponse;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventThreadCleanup;
import org.zkoss.zk.ui.event.EventThreadInit;
import org.zkoss.zk.ui.event.EventThreadResume;
import org.zkoss.zkplus.spring.SpringUtil;

/**
 * <p>Listener to copy servlet thread ThreadLocal, securityContext, over to 
 * event thread ThreadLocal and handle Acegi Authentication Exception occured in
 * Event handling (e.g. Acegi's MethodInterceptor).
 * </p>
 * <p>
 * Whenever you use Acegi as your security provider you have to add following 
 * lines in WEB-INF/zk.xml:
 * <pre><code>
 * 	&lt;listener>
 *		&lt;description>Acegi SecurityContext Handler&lt;/description>
 *		&lt;listener-class>org.zkoss.zkplus.acegi.AcegiSecurityContextListener&lt;/listener-class>
 *	&lt;/listener>
 * </code></pre>
 * </p>
 * <p>Applicable to Acegi Security version 1.0.3</p>
 *
 * @author henrichen
 */
public class AcegiSecurityContextListener implements EventThreadInit, EventThreadCleanup, EventThreadResume {
	private static final Log log = Log.lookup(AcegiSecurityContextListener.class);
	private SecurityContext _context;
	private final boolean _enabled; //whether event thread enabled

	public AcegiSecurityContextListener() {
		final WebApp app = Executions.getCurrent().getDesktop().getWebApp();
		_enabled = app.getConfiguration().isEventThreadEnabled();
	}
	
	//-- EventThreadInit --//
	public void prepare(Component comp, Event evt) {
		if (_enabled) {
			_context = SecurityContextHolder.getContext(); //get threadLocal from servlet thread
		}
	}
	
	public boolean init(Component comp, Event evt) {
		if (_enabled) {
			SecurityContextHolder.setContext(_context); //store into event thread
			_context = null;
		}
		return true;
	}
	
	//-- EventThreadCleanup --//
	public void cleanup(Component comp, Event evt, List errs) {
		if (!_enabled) 
			return;
			
		_context = SecurityContextHolder.getContext(); //get threadLocal from event thread

		//handle Acegi Exception occured within Event handling
		final Execution exec = Executions.getCurrent();
		if (errs != null && !errs.isEmpty() && errs.size() == 1) {
			Throwable ex = (Throwable) errs.get(0);
			if (ex != null) {
				ex = Exceptions.findCause(ex, AcegiSecurityException.class);
				if (ex instanceof AcegiSecurityException) {
					//ZK massage the exception to visual message (not an exception), so
					//we remember the exception in request attribute and let ZkEventExceptionFilter
					//to rethrow the exception so Acegi's ExcepitonTranslationFilter can
					//catch that and show login window.

					//to avoid show the massaged visula message
					errs.clear();

					exec.setAttribute(ZkEventExceptionFilter.EXCEPTION, ex);
					exec.setAttribute(ZkEventExceptionFilter.COMPONENT, comp);
					exec.setAttribute(ZkEventExceptionFilter.EVENT, evt);
				}
			}
		}

		//there was other exception, no need to go thru acegi filter chain.
		if (errs != null && !errs.isEmpty()) return;
		
		//carry the current event that would be used by the filter chain.
		exec.setAttribute(ZkAuthenticationProcessingFilter.CURRENT_EVENT, evt);
		
		Filter filter = (Filter) SpringUtil.getBean("zkFilterChainProxy");
		if (filter != null) {
			ServletRequest request = (ServletRequest) exec.getNativeRequest();
			ServletResponse response = (ServletResponse) exec.getNativeResponse();
			ServletResponse resp = BufferedResponse.getInstance(response, new NullWriter());
			try {
				filter.doFilter(request, resp, new NullFilterChain());
			} catch(Exception ex1) {
				throw UiException.Aide.wrap(ex1); //should never occur
			}
			
			//after filter chain, SecurityContext could have changed
			_context = SecurityContextHolder.getContext(); //get threadLocal from event thread
		}
	}
	
	public void complete(Component comp, Event evt) {
		if (_enabled) {
			SecurityContextHolder.setContext(_context); //store into servlet thread
			_context = null;
		}
	}
	
	//-- EventThreadResume --//
	public void beforeResume(Component comp, Event evt) {
		if (_enabled) {
			_context = SecurityContextHolder.getContext(); //get threadLocal from servlet thread
		}
	}
	
	public void afterResume(Component comp, Event evt) {
		if (_enabled) {
			SecurityContextHolder.setContext(_context); //store into event thread
			_context = null;
		}
	}
	
 	public void abortResume(Component comp, Event evt) {
 		//do nothing
 	}

    private static class NullFilterChain implements FilterChain {
    	public void doFilter(ServletRequest request, ServletResponse response)
        throws java.io.IOException, ServletException {
        	//do nothing
        }
    }
}
