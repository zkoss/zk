/* StackInfo.java

	Purpose:
		
	Description:
		
	History:
		2013/1/18 Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger.impl.info;

import org.zkoss.json.JSONObject;

/**
 * @author dennis
 *
 */
public class StackInfo extends ExecutionInfoBase{

	public static final String TYPE = "stack";
	
	String _name;
	
	public StackInfo(String name,String note) {
		super(TYPE, null,null,note);
		_name = name;
	}
	
	public JSONObject toJSON(){
		JSONObject json = super.toJSON();
		putEssential(json,"name", _name);
		return json;
	}
}
