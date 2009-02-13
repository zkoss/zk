/* AuSetDeferredAttribute.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Dec 17 14:42:15     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.DeferredValue;

/**
 * @deprecated As of release 3.0.5, replace with {@link AuSetAttribute}.
 *
 * @author tomyeh
 * @since 3.0.1
 */
public class AuSetDeferredAttribute extends AuSetAttribute {
	public AuSetDeferredAttribute(Component comp, String attr, DeferredValue val) {
		super(comp, attr, val);
	}
}
