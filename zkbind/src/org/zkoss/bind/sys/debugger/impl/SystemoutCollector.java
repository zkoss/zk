/* SystemoutCollector.java

	Purpose:
		
	Description:
		
	History:
		2013/1/7 Created by Dennis Chen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger.impl;

import java.util.Map;

import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
/**
 * 
 * @author dennis
 *
 */
public class SystemoutCollector implements BindingExecutionInfoCollector{

	@Override
	public void addExecutionInfo(Binding bindingImpl, String type, String fromExpr, String toExpr, Object value,
			Map<String, Object> args) {
		System.out.println(">>"+System.identityHashCode(this)+"["+type+"]: "+fromExpr+" > "+toExpr+" / [value]: "+value);
	}

}
