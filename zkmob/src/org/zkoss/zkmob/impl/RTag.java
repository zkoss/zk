/* RTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 31, 2007 12:45:42 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.impl;

import org.zkoss.zkmob.ResponseTag;

/**
 * The &lt;r> ResponseTag.
 * @author henrichen
 *
 */
public class RTag extends ResponseTag {
	private String _cmd;
	private String[] _data = new String[5];
	private int _dataLength;
	
	public void addKid(ResponseTag tag) {
		if (tag instanceof DTag) {
			_data[_dataLength++] = tag.getValue();
		} else if (tag instanceof CTag) {
			_cmd = tag.getValue();
		}
	}
	
	public String getCommand() {
		return _cmd;
	}
	
	public String[] getData() {
		return _data;
	}
}
