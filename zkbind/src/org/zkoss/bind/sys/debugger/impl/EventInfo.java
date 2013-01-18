/* EventInfo.java

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
public class EventInfo extends ExecutionInfoBase{

	String _event;
	
	public EventInfo(Component comp, String subject,String event,String note) {
		super("event-info", comp, subject, note);
		_event = event;
	}
	
	public JSONObject toJSON(){
		JSONObject json = super.toJSON();
		json.put("event", _event);
		
		return json;
	}
}
