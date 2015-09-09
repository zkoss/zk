/* StringPropertyAccess.java

	Purpose:
		
	Description:
		
	History:
		3:10 PM 9/9/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.sys;

/**
 * A String property class.
 * @author jumperchen
 * @since 8.0.0
 */
public abstract class StringPropertyAccess implements PropertyAccess<String> {
	public Class<String> getType() {
		return String.class;
	}
}
