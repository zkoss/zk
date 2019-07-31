/* ThreadLocalListener.java

	Purpose:

	Description:

	History:
		Sun Jun  10 23:48:51     2007, Created by henrichen

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.util;

import static org.zkoss.lang.Generics.cast;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.lang.SystemException;
import org.zkoss.util.CollectionsX;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventThreadCleanup;
import org.zkoss.zk.ui.event.EventThreadInit;
import org.zkoss.zk.ui.event.EventThreadResume;

/**
 * <p>Listener to make sure servlet thread and ZK event thread got the same ThreadLocal values. You 
 * have to declare this listener in WEB-INF/zk.xml as follows.</p>
 * <pre><code>
 * 	&lt;listener>
 *		&lt;description>ThreadLocal Synchronization Listener&lt;/description>
 *		&lt;listener-class>org.zkoss.zkplus.util.ThreadLocalListener&lt;/listener-class>
 *	&lt;/listener>
 * </code></pre>
 * <p>Besides that, you have to specify what ThreadLocal variables you want to sync. They are also 
 * specified in WEB-INF/zk.xml file in the form as below.</p>
 * <pre><code>
 *  &lt;preference>
 *    &lt;name>ThreadLocal&lt;/name>
 *    &lt;value>
 *			class1=field1,field2,...;
 *			class2=field1,field2,...;
 *			...
 *    &lt;/value>
 *  &lt;/preference>
 * </code></pre>
 * <p>For example, to support synchronizing Spring's thread bounded resources, you have to specify the following 
 * ThreadLocal variables:</p>
 * <pre><code>
 *	&lt;preference>
 *		&lt;name>ThreadLocal&lt;/name>
 *		&lt;value>
 *		org.springframework.transaction.support.TransactionSynchronizationManager=resources,synchronizations,currentTransactionName,currentTransactionReadOnly,actualTransactionActive;
 *		org.springframework.orm.hibernate3.SessionFactoryUtils=deferredCloseHolder;
 *		org.springframework.transaction.interceptor.TransactionAspectSupport=transactionInfoHolder; &lt;!-- ver. 2+ -->
 *		&lt;!--org.springframework.transaction.interceptor.TransactionAspectSupport=currentTransactionInfo; ver. 1.28 -->
 *		&lt;/value>
 *	&lt;/preference>
 * <p>In additions to using the application preference, you can specify it
 * in the library property called <code>zkplus.util.ThreadLocalListener.fieldsMap</code>.
 * The preference has the higher priority.
 * <p>Another example, when you specify the Spring's bean as scope="session", you have to specify the following
 * ThreadLocal variables since Spring 2.0 use RequestContextHolder to handle the bean's scope.</p>
 * <pre><code>
 *	&lt;preference>
 *	&lt;name>ThreadLocal&lt;/name>
 *	&lt;value>
 *		org.springframework.web.context.request.RequestContextHolder=requestAttributesHolder,inheritableRequestAttributesHolder;
 *	&lt;/value>
 * </code></pre>
 *
 * @author henrichen
 * @since 2.4.1
 */
public class ThreadLocalListener implements EventThreadInit, EventThreadCleanup, EventThreadResume {
	private static final Logger log = LoggerFactory.getLogger(ThreadLocalListener.class);
	private Map<String, String[]> _fieldsMap; //(class name, String[] of fields)
	private Map<String, Object[]> _threadLocalsMap; //(class name, ThreadLocal_Contents[] for fields)
	private final boolean _enabled; //whether event thread enabled

	public ThreadLocalListener() {
		final WebApp app = Executions.getCurrent().getDesktop().getWebApp();
		_fieldsMap = cast((Map) app.getAttribute("zkplus.util.ThreadLocalListener.fieldsMap"));
		_enabled = app.getConfiguration().isEventThreadEnabled();
		if (_fieldsMap == null) {
			_fieldsMap = new HashMap<String, String[]>(8);
			final String PREF = "zkplus.util.ThreadLocalListener.fieldsMap";
			app.setAttribute(PREF, _fieldsMap);
			//read preference
			String val = app.getConfiguration().getPreference("ThreadLocal", null);
			if (val == null)
				val = Library.getProperty(PREF);
			if (val != null) {
				final Collection klassSets = CollectionsX.parse(null, val, ';');
				for (Iterator its = klassSets.iterator(); its.hasNext();) {
					final String klassSetStr = (String) its.next();
					final Collection klassSet = CollectionsX.parse(null, klassSetStr, '=');
					final Iterator itz = klassSet.iterator();
					final String klass = (String) itz.next();
					final String fieldsStr = (String) itz.next();
					final Collection<String> fields = CollectionsX.parse(null, fieldsStr, ',');
					_fieldsMap.put(klass, fields.toArray(new String[fields.size()]));
				}
			}
		}
		_threadLocalsMap = new HashMap<String, Object[]>(_fieldsMap.size());
	}

	//-- EventThreadInit --//
	public void prepare(Component comp, Event evt) {
		if (_enabled) {
			getThreadLocals(); //get from servlet thread's ThreadLocal
		}
	}

	public boolean init(Component comp, Event evt) {
		if (_enabled) {
			setThreadLocals(); //copy to event thread's ThreadLocal
		}
		return true;
	}

	//-- EventThreadCleanup --//
	public void cleanup(Component comp, Event evt, List errs) {
		if (_enabled) {
			getThreadLocals(); //get from event thread's ThreadLocal
			//we don't handle the exception since the ZK engine will throw it again!
		}
	}

	public void complete(Component comp, Event evt) {
		if (_enabled) {
			setThreadLocals(); //copy to servlet thread's ThreadLocal
		}
	}

	//-- EventThreadResume --//
	public void beforeResume(Component comp, Event evt) {
		if (_enabled) {
			getThreadLocals(); //get from servlet thread's ThreadLocal
		}
	}

	public void afterResume(Component comp, Event evt) {
		if (_enabled) {
			setThreadLocals(); //copy to event thread's ThreadLocal
		}
	}

	public void abortResume(Component comp, Event evt) {
		//do nothing
	}

	//-- utilities --//
	private void getThreadLocals() {
		for (Entry<String, String[]> me : _fieldsMap.entrySet()) {
			try {
				final String clsName = me.getKey();
				final Class cls = Classes.forNameByThread(clsName);
				final String[] fields = me.getValue();
				final Object[] threadLocals = new Object[fields.length];
				_threadLocalsMap.put(clsName, threadLocals);
				for (int j = 0; j < threadLocals.length; ++j) {
					try {
						threadLocals[j] = getThreadLocal(cls, fields[j]).get();
					} catch (SystemException ex) {
						ex.printStackTrace();
						//ignore
						continue;
					}
				}
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
				//ignore
				continue;
			}
		}
	}

	private void setThreadLocals() {
		for (Entry<String, Object[]> me : _threadLocalsMap.entrySet()) {
			try {
				final String clsName = me.getKey();
				final Class cls = Classes.forNameByThread(clsName);
				final Object[] threadLocals = me.getValue();
				final String[] fields = _fieldsMap.get(clsName);

				for (int j = 0; j < threadLocals.length; ++j) {
					getThreadLocal(cls, fields[j]).set(threadLocals[j]);
				}
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
				continue;
			}
		}
		_threadLocalsMap.clear();
	}

	private ThreadLocal<Object> getThreadLocal(Class cls, String fldname) {
		return cast(ThreadLocals.getThreadLocal(cls, fldname));
	}
}
