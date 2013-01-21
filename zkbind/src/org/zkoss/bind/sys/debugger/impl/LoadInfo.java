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
public class LoadInfo extends ValueInfo{

	String _condition;
	String _fromExpr;
	String _toExpr;
	boolean _formBinding;
	
	public LoadInfo(Component comp, String subject, boolean formBinding,String condition,String fromExpr, String toExpr, Object value,
			Map<String, Object> args,String note) {
		super("load-info", comp, subject, value,note);
		_condition = condition;
		_fromExpr = fromExpr;
		_toExpr = toExpr;
		_formBinding = formBinding;
	}
	public LoadInfo(Component comp, String subject, String condition,String fromExpr, String toExpr, Object value,
			Map<String, Object> args,String note) {
		this(comp, subject, false, condition, fromExpr, toExpr, value, args, note);
	}
	
	public JSONObject toJSON(){
		JSONObject json = super.toJSON();
		json.put("condition", _condition);
		json.put("fromExpr", _fromExpr);
		json.put("toExpr", _toExpr);
		if(_formBinding){
			json.put("formBinding", true);
		}
		return json;
	}

}
