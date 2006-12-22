/* AcegiSecurityContextListener.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep  11 12:55:11     2006, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.acegi;

import org.zkoss.zkplus.spring.SpringUtil;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventThreadInit;
import org.zkoss.zk.ui.event.EventThreadCleanup;
import org.zkoss.zk.ui.event.EventThreadResume;
import org.zkoss.util.logging.Log;

import org.zkoss.lang.Exceptions;

import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.AcegiSecurityException;
import org.acegisecurity.AuthenticationException;

import java.util.List;
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
 *
 * @author henrichen
 */
public class AcegiSecurityContextListener implements EventThreadInit, EventThreadCleanup, EventThreadResume {
	private static final Log log = Log.lookup(AcegiSecurityContextListener.class);
	private SecurityContext _context;

	//-- EventThreadInit --//
	public void prepare(Component comp, Event evt) {
		_context = SecurityContextHolder.getContext(); //get threadLocal from servlet thread
	}
	
	public void init(Component comp, Event evt) {
		SecurityContextHolder.setContext(_context); //store into event thread
		_context = null;
	}
	
	//-- EventThreadCleanup --//
	public void cleanup(Component comp, Event evt, List errs) {
		_context = SecurityContextHolder.getContext(); //get threadLocal from event thread


		//handle Acegi Exception occured within Event handling
		if (errs != null && !errs.isEmpty() && errs.size() == 1) {
			Throwable ex = (Throwable) errs.get(0);
			if (ex != null) {
				ex = Exceptions.findCause(ex, AcegiSecurityException.class);
				if (ex instanceof AcegiSecurityException) {
					final ZkExceptionTranslationHandler handler = 
						(ZkExceptionTranslationHandler) SpringUtil.getBean("zkExceptionTranslationHandler");
					if (handler != null) {
						errs.clear();
						handler.handle(comp, evt, (AcegiSecurityException)ex);
					}
				}
			}
		}
	}
	
	public void complete(Component comp, Event evt) {
		SecurityContextHolder.setContext(_context); //store into servlet thread
		_context = null;
	}
	
	//-- EventThreadResume --//
	public void beforeResume(Component comp, Event evt) {
		_context = SecurityContextHolder.getContext(); //get threadLocal from servlet thread
	}
	
	public void afterResume(Component comp, Event evt) {
		SecurityContextHolder.setContext(_context); //store into event thread
		_context = null;
	}
	
 	public void abortResume(Component comp, Event evt) {
 		//do nothing
 	}
}
