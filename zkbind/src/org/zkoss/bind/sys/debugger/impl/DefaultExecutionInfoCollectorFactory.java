/* DefaultCollectorFactory.java

	Purpose:
		
	Description:
		
	History:
		2013/1/7 Created by Dennis Chen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger.impl;

import org.zkoss.bind.Binder;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollectorFactory;
import org.zkoss.lang.Library;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
/**
 * The default implementation of {@link BindingExecutionInfoCollectorFactory}
 * it is execution scope implementation and provides client-log and system-out implementation
 * @author dennis
 * @since 6.5.2
 */
public class DefaultExecutionInfoCollectorFactory extends BindingExecutionInfoCollectorFactory {

	
	public static final String COLLECTOR_TYPE_PROP = "org.zkoss.bind.DefaultExecutionInfoCollectorFactory.type";
	
	private static final String COLLECTOR_KEY = DefaultExecutionInfoCollectorFactory.class.getName()+".collector";
	private static final Log _log = Log.lookup(DefaultExecutionInfoCollectorFactory.class);
	
	String _type;
	
	@Override
	public BindingExecutionInfoCollector getCollector(Binder binder,Object viewModel) {
		
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

}
