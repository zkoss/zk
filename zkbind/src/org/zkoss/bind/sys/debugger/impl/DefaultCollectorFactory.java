/* DefaultCollectorFactory.java

	Purpose:
		
	Description:
		
	History:
		2013/1/7 Created by Dennis Chen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger.impl;

import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollectorFactory;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;

public class DefaultCollectorFactory extends BindingExecutionInfoCollectorFactory {

	static public final String COLLECTOR_KEY = DefaultCollectorFactory.class.getName()+".collector";
	
	@Override
	public BindingExecutionInfoCollector getCollector() {
		Execution exec = Executions.getCurrent();
		if(exec==null) return null;
		
		BindingExecutionInfoCollector collector = (BindingExecutionInfoCollector)exec.getAttribute(COLLECTOR_KEY);
		if(collector!=null){
			exec.setAttribute(COLLECTOR_KEY,collector = new DefaultCollector());
		}
		return collector;
	}

}
