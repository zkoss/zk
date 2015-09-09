/* BooleanPropertyAccess.java

	Purpose:
		
	Description:
		
	History:
		3:13 PM 9/9/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.sys;

/**
 * A Boolean property access class.
 * @author jumperchen
 * @since 8.0.0
 */
public abstract class BooleanPropertyAccess implements PropertyAccess<Boolean> {
	public Class<Boolean> getType() {
		return Boolean.class;
	}
}
