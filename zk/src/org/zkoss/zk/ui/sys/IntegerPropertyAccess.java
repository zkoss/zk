/* IntegerPropertyAccess.java

	Purpose:
		
	Description:
		
	History:
		3:12 PM 9/9/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.sys;

/**
 * An Integer property class.
 * @author jumperchen
 * @since 8.0.0
 */
public abstract class IntegerPropertyAccess implements PropertyAccess<Integer> {
	public Class<Integer> getType() {
		return Integer.class;
	}
}
