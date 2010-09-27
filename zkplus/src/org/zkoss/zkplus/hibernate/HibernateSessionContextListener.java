/* HibernateSessionContextListener.java

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
package org.zkoss.zkplus.hibernate;

import org.zkoss.zkplus.util.ThreadLocals;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.util.ExecutionInit;
import org.zkoss.zk.ui.util.ExecutionCleanup;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventThreadInit;
import org.zkoss.zk.ui.event.EventThreadResume;
import org.zkoss.lang.Classes;
import org.zkoss.util.logging.Log;
import static org.zkoss.lang.Generics.cast;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Field;

import java.util.List;
/**
 * <p>Listener to make sure each ZK thread got the same hibernat session context; 
 * used with Hibernate's "thread" session context (org.hibernate.context.ThreadLocalSessionContext).
 * </p>
 * <p>
 * This listener is used with Hibernate's (version 3.1+) "thread" session context.
 * That is, when you specify </p>
 * <pre><code>
 * hibernate.current_session_context_class = thread
 * </code></pre>
 *
 * <p>then you have to add following lines in application's WEB-INF/zk.xml:</p>
 * <pre><code>
 * 	&lt;listener>
 *		&lt;description>Hibernate thread session context management&lt;/description>
 *		&lt;listener-class>org.zkoss.zkplus.hibernate.HibernateSessionContextListener&lt;/listener-class>
 *	&lt;/listener>
 * </code></pre>
 * <p>Applicable to Hibernate version 3.2.ga or later</p>
 * @author henrichen
 */
public class HibernateSessionContextListener implements ExecutionInit, ExecutionCleanup, EventThreadInit, EventThreadResume {
	private static final Log log = Log.lookup(HibernateSessionContextListener.class);
	private static final String HIBERNATE_SESSION_MAP = "org.zkoss.zkplus.hibernate.SessionMap";
	private static final Object SOMETHING = new Object();

	private final boolean _enabled; //whether event thread enabled

	public HibernateSessionContextListener() {
		final WebApp app = Executions.getCurrent().getDesktop().getWebApp();
		_enabled = app.getConfiguration().isEventThreadEnabled();
	}
	
	//-- ExecutionInit --//
	public void init(Execution exec, Execution parent) {
		if (_enabled) {
			if (parent == null) { //root execution
				//always prepare a ThreadLocal SessionMap in Execution attribute
				Map<Object, Object> map = getSessionMap();
				if (map == null) {
					map = new HashMap<Object, Object>();
					setSessionMap(map); //copy to servlet thread's ThreadLocal
				}
				exec.setAttribute(HIBERNATE_SESSION_MAP, map); // store in Execution attribute
				
				//20060912, henrichen: tricky. Stuff something into session map to 
				//prevent the map from being removed from context ThreadLocal by the 
				//ThreadLocalSessionContext#unbind() when it is empty.
				map.put(SOMETHING, null); 
			}
		}
	}

	//-- ExecutionCleanup --//
	public void cleanup(Execution exec, Execution parent, List errs) {
		if (_enabled) {
			if (parent == null) { //root execution
				Map<Object, Object> map = getSessionMap();
				if (map != null) {
					//20060912, henrichen: tricky. Remove the previously stuffed 
					//something (when ExecutuionInit#init() is called) from 
					//session map to make the map possible to be removed by the 
					//ThreadLocalSessionContext#unbind() when it is empty.
					map.remove(SOMETHING);
				}
				exec.removeAttribute(HIBERNATE_SESSION_MAP);
			}
		}
	}
	
	//-- EventThreadInit --//
	public void prepare(Component comp, Event evt) {
		//do nothing
	}

	public boolean init(Component comp, Event evt) {
		if (_enabled) {
			//Copy SessionMap stored in Execution attribute into event's ThreadLocal
			Map<Object, Object> map = cast((Map) Executions.getCurrent().getAttribute(HIBERNATE_SESSION_MAP));
			setSessionMap(map); //copy to event thread's ThreadLocal
		}
		return true;
	}

	//-- EventThreadResume --//
	public void beforeResume(Component comp, Event evt) {
		//do nothing
	}

	public void afterResume(Component comp, Event evt) {
		if (_enabled) {
			//always keep the prepared SessionMap in event's ThreadLocal
			Map<Object, Object> map = cast((Map) Executions.getCurrent().getAttribute(HIBERNATE_SESSION_MAP));
			setSessionMap(map); //copy to event thread's ThreadLocal
		}
	}
	
	public void abortResume(Component comp, Event evt){
		//do nothing
	}
	
	//-- utilities --//
	private void setSessionMap(Map<Object, Object> map) {
		getContextThreadLocal().set(map);
	}
	
	private Map<Object, Object> getSessionMap() {
		return getContextThreadLocal().get();
	}
	@SuppressWarnings("unchecked")
	private ThreadLocal<Map<Object, Object>> getContextThreadLocal() {
		return ThreadLocals.getThreadLocal("org.hibernate.context.ThreadLocalSessionContext", "context");
	}
}
