/* HibernateSessionContextListener.java

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
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.ExecutionInit;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventThreadInit;
import org.zkoss.zk.ui.event.EventThreadResume;
import org.zkoss.lang.Classes;
import org.zkoss.util.logging.Log;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Field;


/**
 * <p>Listener to make sure each ZK thread got the same hibernat session context; 
 * used with Hibernate's "thread" session context ({@link org.hibernate.context.ThreadLocalSessionContext}).
 * </p>
 * <p>
 * This listener is used with Hibernate's (version 3.1+) "thread" session context.
 * That is, when you specify 
 * <pre><code>
 * hibernate.current_session_context_class = thread
 * </code></pre>
 *
 * then you have to add following lines in application's WEB-INF/zk.xml:
 * <pre><code>
 * 	&lt;listener>
 *		&lt;description>Hibernate Session Management&lt;/description>
 *		&lt;listener-class>org.zkoss.zkplus.hibernate.HibernateSessionContextListener&lt;/listener-class>
 *	&lt;/listener>
 * </code></pre>
 * </p>
 *
 * @author <a href="mailto:henrichen@potix.com">henrichen@potix.com</a>
 */
public class HibernateSessionContextListener implements ExecutionInit, EventThreadInit, EventThreadResume {
	private static final Log log = Log.lookup(HibernateSessionContextListener.class);
	private static final String HIBERNATE_SESSION_MAP = "org.zkoss.zkplus.hibernate.SessionMap";

	//-- ExecutionInit --//
	public void init(Execution exec, Execution parent) {
		if (parent == null) { //root execution
			//always prepare a ThreadLocal SessionMap in Execution attribute
			Map map = getSessionMap();
			if (map == null) {
				map = new HashMap();
				setSessionMap(map); //copy to servlet thread's ThreadLocal
			}
			exec.setAttribute(HIBERNATE_SESSION_MAP, map); // store in Execution attribute
			
			//20060912, henrichen: tricky. Stuff something into session map to 
			//prevent the map from being removed from context ThreadLocal by the 
			//{@link ThreadLocalSessionContext#unbind()} when it is empty.
			map.put(Boolean.TRUE, null); 
		}
	}
	
	//-- EventThreadInit --//
	public void prepare(Component comp, Event evt) {
		//do nothing
	}
	
	public void init(Component comp, Event evt) {
		//Copy SessionMap stored in Execution attribute into event's ThreadLocal
		Map map = (Map) Executions.getCurrent().getAttribute(HIBERNATE_SESSION_MAP);
		setSessionMap(map); //copy to event thread's ThreadLocal
	}

	//-- EventThreadResume --//
	public void beforeResume(Component comp, Event evt) {
		//do nothing
	}
	
	public void afterResume(Component comp, Event evt) {
		//always keep the prepared SessionMap in event's ThreadLocal
		Map map = (Map) Executions.getCurrent().getAttribute(HIBERNATE_SESSION_MAP);
		setSessionMap(map); //copy to event thread's ThreadLocal
	}
	
	public void abortResume(Component comp, Event evt){
		//do nothing
	}
	
	//-- utilities --//
	private void setSessionMap(Map map) {
		getContextThreadLocal().set(map);
	}
	
	private Map getSessionMap() {
		return (Map) getContextThreadLocal().get();
	}
	
	private ThreadLocal getContextThreadLocal() {
		try {
			Class cls = Classes.forNameByThread("org.hibernate.context.ThreadLocalSessionContext");
			Field fld = cls.getDeclaredField("context");
			fld.setAccessible(true);
			return (ThreadLocal) fld.get(cls); //class static field, a ThreadLocal
		} catch (ClassNotFoundException ex) {
			throw UiException.Aide.wrap(ex);
		} catch (java.lang.NoSuchFieldException ex) {
			throw UiException.Aide.wrap(ex);
		} catch (java.lang.IllegalAccessException ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
}
