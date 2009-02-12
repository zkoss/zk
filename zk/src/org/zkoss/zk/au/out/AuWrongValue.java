/* AuWrongValue.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed May  2 10:51:43     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.au.AuResponse;
/**
 * A response to tell the client a component's value is wrong.
 *
 * <p>data[0]: component's UUID
 * data[1]: the error message
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class AuWrongValue extends AuResponse {
	public AuWrongValue(Component comp, String message) {
		super("wrongValue", comp, new String[] {comp.getUuid(), message});
	}
}
