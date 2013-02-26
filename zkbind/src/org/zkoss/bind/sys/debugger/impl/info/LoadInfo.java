/* LoadInfo.java

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
public class LoadInfo extends ExecutionInfoBase{

	public static final String TYPE = "load";
	
	public static final String FORM_INIT = AddBindingInfo.FORM_INIT;
	public static final String FORM_LOAD = AddBindingInfo.FORM_LOAD;
	
	public static final String PROP_INIT = AddBindingInfo.PROP_INIT;
	public static final String PROP_LOAD = AddBindingInfo.PROP_LOAD;
	
	public static final String CHILDREN_INIT = AddBindingInfo.CHILDREN_INIT;
	public static final String CHILDREN_LOAD = AddBindingInfo.CHILDREN_LOAD;
	
	public static final String REFERENCE = AddBindingInfo.REFERENCE;
	
	String _condition;
	String _fromExpr;
	String _toExpr;
	Object _value;
	
	public LoadInfo(String subtype,Component comp,String condition,String fromExpr, String toExpr, Object value,
			Map<String, Object> args,String note) {
		super(TYPE, subtype,comp,note);
		_condition = condition;
		_fromExpr = fromExpr;
		_toExpr = toExpr;
		_value = value;
	}
	
	public JSONObject toJSON(){
		JSONObject json = super.toJSON();
		put(json,"condition", _condition);
		putEssential(json,"fromExpr", _fromExpr);
		putEssential(json,"toExpr", _toExpr);
		put(json,"value", toString(_value,200));
		if(_value==null){
			put(json,"nullval", true);
		}
		return json;
	}

}
