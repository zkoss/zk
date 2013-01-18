/* NotifyInfo.java

	Purpose:
		
	Description:
		
	History:
		2013/1/18 Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger.impl;

import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;

/**
 * @author dennis
 *
 */
public class NotifyInfo extends ExecutionInfoBase{

	Object _base;
	Object _prop;
	
	public NotifyInfo(Component comp,String subject, Object base, Object prop, String note) {
		super("notify-info", comp, subject, note);
		_base = base;
		_prop = prop;
	}
	
	public JSONObject toJSON(){
		JSONObject json = super.toJSON();
		json.put("base", toString(_base,100));
		json.put("prop", toString(_prop,200));
		return json;
	}

}
