/* LoadInfo.java

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
public class LoadInfo extends ExecutionInfoBase{

	String _condition;
	String _fromExpr;
	String _toExpr;
	Object _value;
	
	public LoadInfo(Component comp, String subject, String condition,String fromExpr, String toExpr, Object value,
			Map<String, Object> args,String note) {
		super("load-info", comp, subject, note);
		_condition = condition;
		_fromExpr = fromExpr;
		_toExpr = toExpr;
		_value = value;
	}
	
	public JSONObject toJSON(){
		JSONObject json = super.toJSON();
		json.put("condition", _condition);
		json.put("fromExpr", _fromExpr);
		json.put("toExpr", _toExpr);
		json.put("value", toString(_value,200));
		return json;
	}

}
