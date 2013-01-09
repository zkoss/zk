/* AbstractExecutionInfoCollector.java

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
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
/**
 * abstract implementation
 * @author dennis
 *
 */
public abstract class AbstractExecutionInfoCollector implements BindingExecutionInfoCollector{

	public abstract void addExecutionInfo(JSONObject info);
	
	@Override
	public void addExecutionInfo(Binding binding, String type, String fromExpr, String toExpr, Object value,
			Map<String, Object> args) {
		JSONObject json = new JSONObject();
		if(binding!=null && binding.getComponent()!=null){
			json.put("widget", binding.getComponent().getDefinition().getName());
			json.put("uuid", binding.getComponent().getUuid());
			json.put("id", binding.getComponent().getId());
		}
		json.put("type", type);
		json.put("fromExpr", fromExpr);
		json.put("toExpr", toExpr);
		String valstr = value==null?null:value.toString();
		if(valstr!=null && valstr.length()>100){
			valstr = valstr.substring(0, 97)+"...";
		}
		json.put("value", valstr);
		
		Execution exec = Executions.getCurrent();
		json.put("sid", exec.getHeader("ZK-SID"));
		
		addExecutionInfo(json);
	}
}
