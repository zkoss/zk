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
public class AddSaveBindingInfo extends ExecutionInfoBase{

	String _condition;
	String _fromExpr;
	String _toExpr;
	
	public AddSaveBindingInfo(Component comp, String subject, String condition,String fromExpr, String toExpr,
			Map<String, Object> args,String note) {
		super("add-save-binding", comp, subject, note);
		_condition = condition;
		_fromExpr = fromExpr;
		_toExpr = toExpr;
	}
	
	public JSONObject toJSON(){
		JSONObject json = super.toJSON();
		json.put("condition", _condition);
		json.put("fromExpr", _fromExpr);
		json.put("toExpr", _toExpr);
		return json;
	}

}
