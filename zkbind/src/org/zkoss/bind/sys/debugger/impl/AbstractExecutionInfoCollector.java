/* AbstractExecutionInfoCollector.java

	Purpose:
		
	Description:
		
	History:
		2013/1/7 Created by Dennis Chen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger.impl;

import java.util.Map;
import java.util.Stack;

import org.zkoss.bind.Validator;
import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
/**
 * abstract implementation
 * @author dennis
 *
 */
public abstract class AbstractExecutionInfoCollector implements BindingExecutionInfoCollector{

	public abstract void addExecutionInfo(JSONObject info);
	
	Stack<String> _infoStack = new Stack<String>();
	
	public void pushStack(String name){
		_infoStack.push(name);
//		Clients.evalJavaScript("console.log('>push "+name+"')");
	}
	public String popStack(){
		String name = _infoStack.pop();
//		Clients.evalJavaScript("console.log('>pop "+name+"')");
		return name;
	}
	
	private JSONObject createJSON(Component comp, String type,String subject, String note){
		JSONObject json = new JSONObject();
		json.put("stack", _infoStack.size());
		json.put("type", type);
		if(comp!=null){
			json.put("widget", comp.getDefinition().getName());
			json.put("uuid", comp.getUuid());
			json.put("id", comp.getId());
		}else{
			json.put("widget", "");
			json.put("uuid", "");
			json.put("id", "");
		}
		json.put("subject", subject);
		json.put("note", note);
		Execution exec = Executions.getCurrent();
		json.put("sid", exec.getHeader("ZK-SID"));
		return json;
	}
	

	@Override
	public void addEnterInfo(Component comp, String subject,String entry,String note) {
		JSONObject json = createJSON(null,"enter-info",subject,note);
		if(comp!=null){
			json.put("widget", comp.getDefinition().getName());
			json.put("uuid", comp.getUuid());
			json.put("id", comp.getId());
		}
		json.put("entry", entry);
		addExecutionInfo(json);
	}
	
	private String toString(Object value, int len){
		String valstr = value==null?null:value.toString();
		if(valstr!=null && valstr.length()>len){
			valstr = valstr.substring(0, len-4)+"...";
		}
		return valstr;
	}
	
	@Override
	public void addLoadInfo(Component comp, String subject, String condition,String fromExpr, String toExpr, Object value,
			Map<String, Object> args,String note) {
		JSONObject json = createJSON(comp,"load-info",subject,note);
		json.put("condition", condition);
		json.put("fromExpr", fromExpr);
		json.put("toExpr", toExpr);
		json.put("value", toString(value,100));

		addExecutionInfo(json);
	}

	@Override
	public void addSaveInfo(Component comp, String subject, String condition,String fromExpr, String toExpr, Object value,
			Map<String, Object> args,String note) {
		JSONObject json = createJSON(comp,"save-info",subject,note);
		json.put("condition", condition);
		json.put("fromExpr", fromExpr);
		json.put("toExpr", toExpr);
		json.put("value", toString(value,100));

		addExecutionInfo(json);
	}

	@Override
	public void addCommandInfo(Component comp,String subject, String event,String commandExpr, Object value,
			Map<String, Object> args,String note) {
		JSONObject json = createJSON(comp,"command-info",subject,note);
		json.put("event", event);
		json.put("commandExpr", commandExpr);
		json.put("value", toString(value,100));

		addExecutionInfo(json);
	}

	@Override
	public void addValidationInfo(Component comp,String subject, String validatorExpr, Validator validator,
			Object result,Map<String, Object> args,String note) {
		JSONObject json = createJSON(comp,"validation-info",subject,note);
		
		json.put("validatorExpr", validatorExpr);
		json.put("validator", toString(validator,100));
		json.put("result", toString(result,100));

		addExecutionInfo(json);
	}
	

	@Override
	public void addNotifyInfo(Component comp,String subject, Object base, Object prop, String note) {
		JSONObject json = createJSON(comp,"notify-info",subject,note);
		
		json.put("base", toString(base,100));
		json.put("prop", toString(prop,100));

		addExecutionInfo(json);
	}
}
