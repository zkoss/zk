/* CommandInfo.java

	Purpose:
		
	Description:
		
	History:
		2013/1/18 Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger.impl.info;

import java.util.Map;

import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;

/**
 * @author dennis
 *
 */
public class CommandInfo extends ExecutionInfoBase{

	public static final String TYPE = "command";
	
	public static final String ON_COMMAND = "on-command";
	public static final String ON_GLOBAL_COMMAND = "on-command-global";
	public static final String EXECUTE = "execute";
	public static final String EXECUTE_GLOBAL = "execute-global";
	public static final String POST = "post";
	public static final String POST_GLOBAL = "post-global";

	
	
	String _event;
	String _commandExpr;
	String _command;
	
	public CommandInfo(String subtype, Component comp,String event, String commandExpr, String command,
			Map<String, Object> args,String note) {
		super(TYPE, subtype, comp,note);
		_event = event;
		_commandExpr = commandExpr;
		_command = command;
	}
	
	public JSONObject toJSON(){
		JSONObject json = super.toJSON();
		put(json,"event", _event);
		put(json,"commandExpr", _commandExpr);
		put(json,"command", _command);
		return json;
	}

}
