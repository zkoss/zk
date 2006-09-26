/* AcegiSecurityContextListener.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep  11 12:55:11     2006, Created by henrichen@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.hibernate;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventThreadInit;
import org.zkoss.zk.ui.event.EventThreadCleanup;
import org.zkoss.zk.ui.event.EventThreadResume;
import org.zkoss.util.logging.Log;

import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;

/**
 * <p>Listener to copy servlet thread ThreadLocal, securityContext, over to 
 * event thread ThreadLocal.
 * </p>
 * <p>
 * Whenever you use Acegi as your security provider you have to add following 
 * lines in WEB-INF/zk.xml:
 * <pre><code>
 * 	&lt;listener>
 *		&lt;description>Acegi SecurityContext Management&lt;/description>
 *		&lt;listener-class>org.zkoss.zkplus.acegi.AcegiSecurityContextListener&lt;/listener-class>
 *	&lt;/listener>
 * </code></pre>
 * </p>
 *
 * @author <a href="mailto:henrichen@potix.com">henrichen@potix.com</a>
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
	public void cleanup(Component comp, Event evt, Throwable ex) {
		_context = SecurityContextHolder.getContext(); //get threadLocal from event thread
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
