/* TypeConverter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Nov 16 12:43:01     2006, Created by Henri Chen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import org.zkoss.zk.ui.Component;

/**
 * Converter to cast object class between UI Component attribute and backend data bean property.
 * This interface is used in DataBinder. The implementation of this converter should not hold any
 * state since it is shared by the whole application.
 *
 * @author Henri Chen
 * @deprecated As of release 7.0.0, replace with new ZK binding.
 */
public interface TypeConverter {
	/** Special Value, when returned in {@link #coerceToBean} or {@link #coerceToUi},
	 * meaning ignore it.
	 */
	public static final Object IGNORE = new Object();

	/** Convert an value object to UI component attribute type.
	 * @param val the object to be coerced to UI component attribute type.
	 * @param comp associated component
	 * @return the converted value suitable for assigning into UI component attribute; or {@link #IGNORE} 
	 * if you want DataBinder to ignore the assignment.
	 */
	public Object coerceToUi(Object val, Component comp);

	/** Convert an value object to bean property type.
	 * @param val the object to be coerced to backend bean property type.
	 * @param comp associated component
	 * @return the converted value suitable for assigning into backend bean property; or {@link #IGNORE} 
	 * if you want DataBinder to ignore the assignment.
	 */
	public Object coerceToBean(Object val, Component comp);
}
