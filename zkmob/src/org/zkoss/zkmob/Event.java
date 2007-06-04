/* Event.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 30, 2007 1:44:23 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob;

/**
 * Event to be sent back to ZK server.
 * @author henrichen
 *
 */
public class Event {
	private String _uuid;
	private String _cmd;
	private Object[] _data;
	
	private boolean _implicit;
	
	public Event(String uuid, String cmd, Object[] data) {
		_uuid = uuid;
		_cmd = cmd;
		_data = data;
	}
	
	public String getUuid() {
		return _uuid;
	}
	
	public String getCmd() {
		return _cmd;
	}
	
	public Object[] getData() {
		return _data;
	}
	
	public void setImplicit(boolean b) {
		_implicit = b;
	}
	
	public boolean isImplicit() {
		return _implicit;
	}
}
