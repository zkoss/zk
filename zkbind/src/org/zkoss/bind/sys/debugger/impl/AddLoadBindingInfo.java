/* AddLoadBindingInfo.java

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
public class AddLoadBindingInfo extends LocationInfoBase{

	String _condition;
	String _fromExpr;
	String _toExpr;
	boolean _formBinding;
	
	public AddLoadBindingInfo(Component comp, String subject, boolean formBinding, String condition,String fromExpr, String toExpr,
			Map<String, Object> args,String note) {
		super("add-load-binding", comp, subject, note);
		_condition = condition;
		_fromExpr = fromExpr;
		_toExpr = toExpr;
		_formBinding = formBinding;
	}
	
	public AddLoadBindingInfo(Component comp, String subject, String condition,String fromExpr, String toExpr,
			Map<String, Object> args,String note) {
		this(comp, subject, false, condition, fromExpr, toExpr, args, note);
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
