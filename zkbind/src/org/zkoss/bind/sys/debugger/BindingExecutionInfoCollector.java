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

	void addExecutionInfo(Binding bindingImpl, String type, String fromExpr, String toExpr,
			Object value, Map<String,Object> args);

}
