/* BindingExecutionInfoCollector.java

	Purpose:
		
	Description:
		
	History:
		2013/1/7 Created by Dennis Chen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger;

import java.util.Map;

import org.zkoss.bind.Validator;
import org.zkoss.bind.sys.Binding;
import org.zkoss.zk.ui.Component;

/**
 * the collector to collect runtime binding execution information
 * @author dennis
 * @since 6.5.2
 */
public interface BindingExecutionInfoCollector {

	
	void pushStack(String name);
	String popStack();
	
	void addLoadInfo(Binding binding, String subject, String condition, String fromExpr, String toExpr,
			Object value, Map<String,Object> args,String note);
	void addSaveInfo(Binding binding, String subject, String condition, String fromExpr, String toExpr,
			Object value, Map<String,Object> args,String note);
	void addCommandInfo(Binding binding, String subject, String event,String commandExpr,
			Object value, Map<String,Object> args,String note);
	void addValidationInfo(Binding binding, String subject, String validatorExpr, Validator validator,
			Object result, Map<String,Object> args,String note);
	void addNotifyInfo(String subject, Object base,Object prop,String note);
	void addEnterInfo(Component comp, String subject,String entry,String note);
}
