/* CommandInfo.java

	Purpose:
		
	Description:
		
	History:
		2013/1/18 Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger.impl;

import java.util.Map;

import org.zkoss.bind.Validator;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;

/**
 * @author dennis
 *
 */
public class ValidationInfo extends ExecutionInfoBase{

	String _validatorExpr;
	Validator _validator;
	Object _result;
	
	public ValidationInfo(Component comp,String subject, String validatorExpr, Validator validator,
			Object result,Map<String, Object> args,String note) {
		super("validation-info", comp, subject, note);
		_validatorExpr = validatorExpr;
		_validator = validator;
		_result = result;
	}
	
	public JSONObject toJSON(){
		JSONObject json = super.toJSON();
		json.put("validatorExpr", _validatorExpr);
		json.put("validator", toString(_validator,200));
		if(_result instanceof Boolean){
			json.put("result", _result);
		}else{
			json.put("result", toString(_result,200));
		}
		return json;
	}

}
