/* AddSaveBindingInfo.java

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
public class AddCommandBindingInfo extends ExecutionInfoBase{

	public static final String TYPE = "add-command-binding";
	
	public static final String VIEWMODEL = "viewmodel";
	public static final String GLOBAL = "global";
	
	String _event;
	String _commandExpr;
	
	public AddCommandBindingInfo(String subtype,Component comp,String event,String commandExpr,
			Map<String, Object> args,String note) {
		super(TYPE, subtype, comp, note);
		_event = event;
		_commandExpr = commandExpr;
	}
	
	public JSONObject toJSON(){
		JSONObject json = super.toJSON();
		put(json,"event", _event);
		put(json,"commandExpr", _commandExpr);
		return json;
	}

}
