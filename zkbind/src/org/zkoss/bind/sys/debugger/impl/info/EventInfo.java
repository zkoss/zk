/* EventInfo.java

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
public class EventInfo extends ExecutionInfoBase{

	public static final String TYPE = "event";
	String _event;
	
	public EventInfo(Component comp, String event,String note) {
		super(TYPE, null, comp, note);
		_event = event;
	}
	
	public JSONObject toJSON(){
		JSONObject json = super.toJSON();
		putEssential(json,"event", _event);
		
		return json;
	}
}
