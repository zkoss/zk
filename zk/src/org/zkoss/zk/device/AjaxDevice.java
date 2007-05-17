/* AjaxDevice.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 14 19:13:07     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.device;

import org.zkoss.zk.ui.Desktop;

/**
 * Represents a Web browser with the Ajax support.
 *
 * @author tomyeh
 */
public class AjaxDevice implements Device, java.io.Serializable {
	private String _type;
	private String _uamsg;

	//Device//
	public String getType() {
		return _type;
	}
	public String getUnavailableMessage() {
		return _uamsg;
	}
	public void setUnavailableMessage(String unavailmsg) {
		_uamsg = unavailmsg;
	}
	public void init(String type, Desktop desktop, String unavailmsg) {
		if (type == null || type.length() == 0)
			throw new IllegalArgumentException("type");
		_type = type;
		_uamsg = unavailmsg;
	}
	public void sessionWillPassivate(Desktop desktop) {
	}
	public void sessionDidActivate(Desktop desktop) {
	}
}
