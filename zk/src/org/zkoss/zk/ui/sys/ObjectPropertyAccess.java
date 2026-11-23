/* ObjectPropertyAccess.java

	Purpose:
		
	Description:
		
	History:
		3:17 PM 9/9/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.sys;

/**
 * An Object proeprty type
 * @author jumperchen
 * @since 8.0.0
 */
public abstract class ObjectPropertyAccess implements PropertyAccess<Object> {
	public Class<Object> getType() {
		return Object.class;
	}
}
