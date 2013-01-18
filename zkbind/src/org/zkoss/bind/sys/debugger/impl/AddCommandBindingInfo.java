/* AddSaveBindingInfo.java

	Purpose:
		
	Description:
		
	History:
		2013/1/18 Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger.impl;

import java.util.Map;

import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;

/**
 * @author dennis
 *
 */
public class AddCommandBindingInfo extends ExecutionInfoBase{

	String _event;
	String _commandExpr;
	
	public AddCommandBindingInfo(Component comp,String subject, String event,String commandExpr,
			Map<String, Object> args,String note) {
		super("add-command-binding", comp, subject, note);
		_event = event;
		_commandExpr = commandExpr;
	}
	
	public JSONObject toJSON(){
		JSONObject json = super.toJSON();
		json.put("event", _event);
		json.put("commandExpr", _commandExpr);
		return json;
	}

}
