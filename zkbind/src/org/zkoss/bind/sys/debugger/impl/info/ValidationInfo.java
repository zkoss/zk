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
public class ValidationInfo extends ExecutionInfoBase{

	public static final String TYPE = "validation";
	
	public static final String FORM="form";
	public static final String PROP="prop";
	
	String _validatorExpr;
	String _validator;
	Object _result;
	
	public ValidationInfo(String subtype,Component comp,String validatorExpr, String validator,
			Object result,Map<String, Object> args,String note) {
		super(TYPE, subtype,comp,note);
		_validatorExpr = validatorExpr;
		_validator = validator;
		_result = result;
	}
	
	public JSONObject toJSON(){
		JSONObject json = super.toJSON();
		putEssential(json,"validatorExpr", _validatorExpr);
		putEssential(json,"validator", toString(_validator,200));
		if(_result instanceof Boolean){
			put(json,"result", _result);
		}else{
			put(json,"result", toString(_result,200));
		}
		return json;
	}

}
