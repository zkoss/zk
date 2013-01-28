/* DefaultCollectorFactory.java

	Purpose:
		
	Description:
		
	History:
		2013/1/7 Created by Dennis Chen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger.impl;

import org.zkoss.bind.Binder;
import org.zkoss.bind.sys.debugger.BindingAnnotationInfoChecker;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.bind.sys.debugger.DebuggerFactory;
import org.zkoss.lang.Library;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
/**
 * The default implementation of {@link DebuggerFactory}
 * it is execution scope implementation and provides client-log and system-out implementation
 * @author dennis
 * @since 6.5.2
 */
public class DefaultDebuggerFactory extends DebuggerFactory {

	
	public static final String COLLECTOR_TYPE_PROP = "org.zkoss.bind.DefaultDefaultDebuggerFactory.collector-type";
	
	private static final String COLLECTOR_KEY = DefaultDebuggerFactory.class.getName()+".collector";
	private static final String CHECKER_KEY = DefaultDebuggerFactory.class.getName()+".checker";
	private static final Log _log = Log.lookup(DefaultDebuggerFactory.class);
	
	String _type;
	
	@Override
	public BindingExecutionInfoCollector getExecutionInfoCollector(Object target) {
		
		Execution exec = Executions.getCurrent();
		if(exec==null) return null;
		
		BindingExecutionInfoCollector collector = (BindingExecutionInfoCollector)exec.getAttribute(COLLECTOR_KEY);
		if(collector==null){
			if(_type==null){
				synchronized(this){
					if(_type==null){
						_type = Library.getProperty(COLLECTOR_TYPE_PROP,"system-out");
					}
				}
			}
			if("client".equals(_type)){
				collector = new ClientExecutionInfoCollector();
			}else if("client-informer".equals(_type)){
				collector = new ClientInformerExecutionInfoCollector();
			}else if("system-out".equals(_type)){
				collector = new SystemOutExecutionInfoCollector();
			}else{
				//default client
				_log.warning("unknow type :"+_type+", use default system-out implementation");
				collector = new SystemOutExecutionInfoCollector();
			}
			exec.setAttribute(COLLECTOR_KEY,collector);
		}
		return collector;
	}

	@Override
	public BindingAnnotationInfoChecker getAnnotationInfoChecker(Object target) {
		Execution exec = Executions.getCurrent();
		if(exec==null) return null;
		BindingExecutionInfoCollector collector = getExecutionInfoCollector(target);
		if(collector==null) return null;
		
		BindingAnnotationInfoChecker checker = (BindingAnnotationInfoChecker)exec.getAttribute(CHECKER_KEY);
		if(checker==null){
			checker = new DefaultAnnotationInfoChecker(collector);
			exec.setAttribute(CHECKER_KEY,checker);
		}
		return checker;
	}

}
