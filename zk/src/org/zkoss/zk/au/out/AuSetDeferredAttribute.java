/* AuSetDeferredAttribute.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Dec 17 14:42:15     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.util.DeferredValue;

/**
 * A response to set the deferred attribute of the specified component
 * at the client.
 * <p>data[0]: the uuid of the component<br/>
 * data[1]: the attribute name<br/>
 * data[2]: the attribute value
 *
 * @author tomyeh
 * @since 3.0.1
 */
public class AuSetDeferredAttribute extends AuResponse {
	private DeferredValue _val;

	public AuSetDeferredAttribute(Component comp, String attr, DeferredValue val) {
		super("setAttr", comp, new String[] {comp.getUuid(), attr, null});
		if (val == null)
			throw new IllegalArgumentException();
		_val = val;
	}
	public String getCommand() {
		eval();
		return super.getCommand();
	}
	public String[] getData() {
		eval();
		return super.getData();
	}
	private void eval() {
		if (_val != null) {
			_data[2] = _val.getValue();
			if (_data[2] == null) {
				_cmd = "rmAttr";
				_data = new String[] {_data[0], _data[1]};
			}
			_val = null;
		}
	}
}
