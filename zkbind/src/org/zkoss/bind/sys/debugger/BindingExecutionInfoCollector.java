/* BindingExecutionInfoCollector.java

	Purpose:
		
	Description:
		
	History:
		2013/1/7 Created by Dennis Chen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger;

import java.util.Map;

import org.zkoss.bind.sys.Binding;

/**
 * the collector to collect runtime binding execution information
 * @author dennis
 * @since 6.5.2
 */
public interface BindingExecutionInfoCollector {

	/**
	 * add binding execution info to this collector
	 * @param binding the binding 
	 * @param type the binding type
	 * @param fromExpr the from expression
	 * @param toExpr the to expression
	 * @param value the value
	 * @param args the args of binding
	 */
	void addExecutionInfo(Binding binding, String type, String fromExpr, String toExpr,
			Object value, Map<String,Object> args);
}
