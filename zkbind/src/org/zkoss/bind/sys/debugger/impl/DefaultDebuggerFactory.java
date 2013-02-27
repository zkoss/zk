/* DefaultCollectorFactory.java

	Purpose:
		
	Description:
		
	History:
		2013/1/7 Created by Dennis Chen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger.impl;

import org.zkoss.bind.sys.debugger.BindingAnnotationInfoChecker;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.bind.sys.debugger.DebuggerFactory;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
/**
 * The default implementation of {@link DebuggerFactory}
 * it is execution scope implementation and provides client-log and system-out implementation
 * @author dennis
 * @since 6.5.2
 */
public class DefaultDebuggerFactory extends DebuggerFactory {
	
	private static final String COLLECTOR_KEY = DefaultDebuggerFactory.class.getName()+".collector";
	private static final String CHECKER_KEY = DefaultDebuggerFactory.class.getName()+".checker";
//	private static final Log _log = Log.lookup(DefaultDebuggerFactory.class);
	
	String _type;
	
	@Override
	public BindingExecutionInfoCollector getExecutionInfoCollector() {
		
		Execution exec = Executions.getCurrent();
		if(exec==null) return null;
		
		BindingExecutionInfoCollector collector = (BindingExecutionInfoCollector)exec.getAttribute(COLLECTOR_KEY);
		if(collector==null){
			collector = createBindingExecutionInfoCollector();
			exec.setAttribute(COLLECTOR_KEY,collector);
		}
		return collector;
	}

	protected BindingExecutionInfoCollector createBindingExecutionInfoCollector() {
		return new DefaultExecutionInfoCollector();
	}

	@Override
	public BindingAnnotationInfoChecker getAnnotationInfoChecker() {
		Execution exec = Executions.getCurrent();
		if(exec==null) return null;
		
		BindingAnnotationInfoChecker checker = (BindingAnnotationInfoChecker)exec.getAttribute(CHECKER_KEY);
		if(checker==null){
			checker = createDefaultAnnotationInfoChecker();
			exec.setAttribute(CHECKER_KEY,checker);
		}
		return checker;
	}

	protected BindingAnnotationInfoChecker createDefaultAnnotationInfoChecker() {
		return new DefaultAnnotationInfoChecker(getExecutionInfoCollector());
	}

}
