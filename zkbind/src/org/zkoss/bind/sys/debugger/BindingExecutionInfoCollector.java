/* BindingExecutionInfoCollector.java

	Purpose:
		
	Description:
		
	History:
		2013/1/7 Created by Dennis Chen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.sys.debugger;


/**
 * the collector to collect runtime binding execution information
 * 
 * @author dennis
 * @since 6.5.2
 */
public interface BindingExecutionInfoCollector {

	void pushStack(String name);

	String popStack();
	
	void addInfo(ExecutionInfo info);
}
