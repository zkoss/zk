/* DynamicPropertied.java

{{IS_NOTE
	$Id: DynamicPropertied.java,v 1.1 2006/05/08 05:56:30 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Mon Oct  3 22:03:28     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.ext;

import com.potix.zk.ui.WrongValueException;

/**
 * Represents a component that supports a range of properties.
 * It is used to simplify the design of a component, such that
 * developers need to implement member functions (setter and getter)
 * for each property it supports.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.1 $ $Date: 2006/05/08 05:56:30 $
 */
public interface DynamicPropertied {
	/** Returns whether a dynamic attribute is allowed.
	 */
	public boolean hasDynamicProperty(String name);
	/** Sets an attribute with a value.
	 *
	 * <p>If a component supports only String-type values, it could
	 * use com.potix.lang.Objects.toString() to convert the value
	 * to a String instance.
	 */
	public void setDynamicProperty(String name, Object value)
	throws WrongValueException;
}
