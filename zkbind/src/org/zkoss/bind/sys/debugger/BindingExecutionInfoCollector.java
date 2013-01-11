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
 * 
 * @author dennis
 * @since 6.5.2
 */
public interface BindingExecutionInfoCollector {

	void pushStack(String name);

	String popStack();

	void addLoadInfo(Component comp, String subject, String condition, String fromExpr, String toExpr, Object value,
			Map<String, Object> args, String note);

	void addSaveInfo(Component comp, String subject, String condition, String fromExpr, String toExpr, Object value,
			Map<String, Object> args, String note);

	void addCommandInfo(Component comp, String subject, String event, String commandExpr, Object value,
			Map<String, Object> args, String note);

	void addValidationInfo(Component comp, String subject, String validatorExpr, Validator validator, Object result,
			Map<String, Object> args, String note);

	void addNotifyInfo(Component comp, String subject, Object base, Object prop, String note);

	void addEnterInfo(Component comp, String subject, String entry, String note);

	void addLoadBinding(Component comp, String subject, String condition, String fromExpr, String toExpr, String note);

	void addSaveBinding(Component comp, String subject, String condition, String fromExpr, String toExpr, String note);

	void addCommandBinding(Component comp, String subject, String event, String commandExpr, String note);
}
