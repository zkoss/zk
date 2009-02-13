/* AuSetAttribute.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 11:28:05     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.util.DeferredValue;

/**
 * A response to set the attribute of the specified component at the client.
 * <p>data[0]: the uuid of the component<br/>
 * data[1]: the attribute name<br/>
 * data[2]: the attribute value
 *
 * <p>If val is null, it is the same as {@link AuRemoveAttribute}.
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class AuSetAttribute extends AuResponse {
	public AuSetAttribute(Component comp, String attr, String val) {
		super("setAttr", comp, new String[] {comp.getUuid(), attr, val});
	}
	public AuSetAttribute(Component comp, String attr, DeferredValue val) {
		super("setAttr", comp, new Object[] {comp.getUuid(), attr, val});
	}
	public AuSetAttribute(Component comp, String attr, Object[] values) {
		super("setAttr", comp, toData(comp, attr, values));
	}
	private static final
	Object[] toData(Component comp, String attr, Object[] values) {
		final int len = values != null ? values.length: 0;
		final Object[] data = new Object[len + 2];
		data[0] = comp.getUuid();
		data[1] = attr;
		for (int j = 0; j < len; ++j)
			data[j + 2] = values[j];
		return data;
	}
}
