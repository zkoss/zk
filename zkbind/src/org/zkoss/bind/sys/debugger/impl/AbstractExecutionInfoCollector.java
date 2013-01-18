/* AbstractExecutionInfoCollector.java

	Purpose:
		
	Description:
		
	History:
		2013/1/7 Created by Dennis Chen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger.impl;

import java.util.Stack;

import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.bind.sys.debugger.ExecutionInfo;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
/**
 * abstract implementation
 * @author dennis
 *
 */
public abstract class AbstractExecutionInfoCollector implements BindingExecutionInfoCollector{

	public abstract void addInfo(JSONObject info);
	
	Stack<String> _infoStack = new Stack<String>();
	
	public void pushStack(String name){
		_infoStack.push(name);
	}
	public String popStack(){
		String name = _infoStack.pop();
		return name;
	}
	
	
	public void addInfo(ExecutionInfo info){
		JSONObject json = ((ExecutionInfoBase)info).toJSON();
		json.put("stack", _infoStack.size());
		Execution exec = Executions.getCurrent();
		String sid = exec.getHeader("ZK-SID");
		int sid0 = 0;
		try{
			sid0 = sid==null?sid0:Integer.parseInt(sid);
		}catch(Exception x){}
		json.put("sid", Integer.valueOf(sid0));
		
		addInfo(json);
	}
	
	
}
