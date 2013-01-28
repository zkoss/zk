/* ValueInfo.java

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
class ValueInfo extends LocationInfoBase{

	Object _value;
	
	protected ValueInfo(String type,Component comp, String subject,Object value,String note) {
		super(type, comp, subject, note);
		_value = value;
	}
	
	public JSONObject toJSON(){
		JSONObject json = super.toJSON();
		json.put("value", toString(_value,200));
		if(_value==null){
			json.put("nullval", true);
		}
		return json;
	}

}
