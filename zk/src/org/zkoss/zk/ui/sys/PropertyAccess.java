/* PropertyAccess.java

	Purpose:
		
	Description:
		
	History:
		12:43 PM 9/9/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.sys;

import java.io.Serializable;

import org.zkoss.zk.ui.Component;

/**
 * A property access interface to speed up Java reflection performance.
 * @author jumperchen
 * @since 8.0.0
 */
public interface PropertyAccess<T> extends Serializable {
	public void setValue(Component cmp, T value);
	public Class<T> getType();
	public T getValue(Component cmp);
}
