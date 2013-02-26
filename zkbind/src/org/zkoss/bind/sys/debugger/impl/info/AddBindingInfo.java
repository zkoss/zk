/* AddLoadBindingInfo.java

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
public class AddBindingInfo extends ExecutionInfoBase{

	public static final String TYPE = "add-binding";
	
	public static final String FORM_INIT = "form-init";
	public static final String FORM_LOAD = "form-load";
	public static final String FORM_SAVE = "form-save";
	
	public static final String PROP_INIT = "prop-init";
	public static final String PROP_LOAD = "prop-load";
	public static final String PROP_SAVE = "prop-save";
	
	public static final String CHILDREN_INIT = "children-init";
	public static final String CHILDREN_LOAD = "children-load";
	public static final String REFERENCE = "reference";
	
	
	String _condition;
	String _fromExpr;
	String _toExpr;
	String _bindingType;
	
	public AddBindingInfo(String subtype,Component comp, String condition,String fromExpr, String toExpr,
			Map<String, Object> args,String note) {
		super(TYPE, subtype,comp, note);
		_condition = condition;
		_fromExpr = fromExpr;
		_toExpr = toExpr;
	}
	
	public JSONObject toJSON(){
		JSONObject json = super.toJSON();
		putEssential(json,"fromExpr", _fromExpr);
		putEssential(json,"toExpr", _toExpr);
		put(json,"condition", _condition);
		return json;
	}

}
