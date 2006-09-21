/* SpringTransactionSynchronizationListener.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep  15 13:55:11     2006, Created by henrichen@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.spring;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventThreadInit;
import org.zkoss.zk.ui.event.EventThreadResume;
import org.zkoss.zk.ui.event.EventThreadCleanup;
import org.zkoss.lang.Classes;
import org.zkoss.lang.ThreadLocals;
import org.zkoss.util.logging.Log;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Field;


/**
 * <p>Listener to make sure each ZK thread got the same ThreadLocal value of the 
 * spring ({@link org.springframework.transaction.support.TransactionSynchronizationManager};
 * especially those thread bound resources.
 * </p>
 * <p>
 * This listener is used with Spring Framework (version 1.2.8+) "thread" bounded
 * resources.
 *
 * <pre><code>
 * 	&lt;listener>
 *		&lt;description>Spring TransactionSynchronizationManager handler&lt;/description>
 *		&lt;listener-class>org.zkoss.zkplus.spring.SpringTransactionSynchronizationListener&lt;/listener-class>
 *	&lt;/listener>
 * </code></pre>
 * </p>
 *
 * @author <a href="mailto:henrichen@potix.com">henrichen@potix.com</a>
 */
public class SpringTransactionSynchronizationListener implements EventThreadInit, EventThreadCleanup, EventThreadResume {
	private static final Log log = Log.lookup(SpringTransactionSynchronizationListener.class);
	
	private Object[] _threadLocals = null;

	//-- EventThreadInit --//
	public void prepare(Component comp, Event evt) {
		getThreadLocals(); //get from servlet thread's ThreadLocal
	}
	
	public void init(Component comp, Event evt) {
		setThreadLocals(); //copy to event thread's ThreadLocal
	}

	//-- EventThreadCleanup --//
	public void cleanup(Component comp, Event evt, Throwable ex) {
		getThreadLocals(); //get from event thread's ThreadLocal
		//we don't handle the exception since the ZK engine will throw it again!
	}

	public void complete(Component comp, Event evt) {
		setThreadLocals(); //copy to servlet thread's ThreadLocal
	}

	//-- EventThreadResume --//
	public void beforeResume(Component comp, Event evt) {
		getThreadLocals(); //get from servlet thread's ThreadLocal
	}
	
	public void afterResume(Component comp, Event evt) {
		setThreadLocals(); //copy to event thread's ThreadLocal
	}
	
	public void abortResume(Component comp, Event evt){
		//do nothing
	}
	
	//-- utilities --//
	private void getThreadLocals() {
		try {
			Class cls = Classes.forNameByThread("org.springframework.transaction.support.TransactionSynchronizationManager");
	
			_threadLocals = new Object[7];
			_threadLocals[0] = getThreadLocal(cls, "resources").get();
			_threadLocals[1] = getThreadLocal(cls, "synchronizations").get();
			_threadLocals[2] = getThreadLocal(cls, "currentTransactionName").get();
			_threadLocals[3] = getThreadLocal(cls, "currentTransactionReadOnly").get();
			_threadLocals[4] = getThreadLocal(cls, "actualTransactionActive").get();
			
			cls = Classes.forNameByThread("org.springframework.orm.hibernate3.SessionFactoryUtils");
			_threadLocals[5] = getThreadLocal(cls, "deferredCloseHolder").get();
			
			cls = Classes.forNameByThread("org.springframework.transaction.interceptor.TransactionAspectSupport");
			_threadLocals[6] = getThreadLocal(cls, "currentTransactionInfo").get();
			
		} catch (ClassNotFoundException ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	
	private void setThreadLocals() {
		if (_threadLocals != null) {
			try {
				Class cls = Classes.forNameByThread("org.springframework.transaction.support.TransactionSynchronizationManager");
		
				getThreadLocal(cls, "resources").set(_threadLocals[0]);
				getThreadLocal(cls, "synchronizations").set(_threadLocals[1]);
				getThreadLocal(cls, "currentTransactionName").set(_threadLocals[2]);
				getThreadLocal(cls, "currentTransactionReadOnly").set(_threadLocals[3]);
				getThreadLocal(cls, "actualTransactionActive").set(_threadLocals[4]);
				
				cls = Classes.forNameByThread("org.springframework.orm.hibernate3.SessionFactoryUtils");
				getThreadLocal(cls, "deferredCloseHolder").set(_threadLocals[5]);

				cls = Classes.forNameByThread("org.springframework.transaction.interceptor.TransactionAspectSupport");
				getThreadLocal(cls, "currentTransactionInfo").set(_threadLocals[6]);
				
				_threadLocals = null;
			} catch (ClassNotFoundException ex) {
				throw UiException.Aide.wrap(ex);
			}
		}
	}
		
	private ThreadLocal getThreadLocal(Class cls, String fldname) {
		return ThreadLocals.getThreadLocal(cls, fldname);
	}
}
