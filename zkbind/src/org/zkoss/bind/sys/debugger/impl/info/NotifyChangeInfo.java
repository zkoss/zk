/* NotifyInfo.java

	Purpose:
		
	Description:
		
	History:
		2013/1/18 Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger.impl.info;

import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;

/**
 * @author dennis
 *
 */
public class NotifyChangeInfo extends ExecutionInfoBase{

	public static final String TYPE = "notify-change";
	
	Object _base;
	Object _prop;
	
	public NotifyChangeInfo(Component comp, Object base, Object prop, String note) {
		super(TYPE, null,comp, note);
		_base = base;
		_prop = prop;
	}
	
	public JSONObject toJSON(){
		JSONObject json = super.toJSON();
		putEssential(json,"base", toString(_base,100));
		putEssential(json,"prop", toString(_prop,200));
		return json;
	}

}
