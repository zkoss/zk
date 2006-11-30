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

/**
 * Converter to cast object class between UI Component attribute and backend data bean property.
 * This interface is used in DataBinder.
 *
 * @author Henri Chen
 */
public interface TypeConverter {
	/** Convert an value object to UI component attribute type.
	 * @param val the object to be corece to UI component attribute type.
	 */
	public Object coerceToUi(Object val) throws ClassCastException;

	/** Convert an value object to bean property type.
	 * @param val the object to be corece to backend bean property type.
	 */
	public Object coerceToBean(Object val) throws ClassCastException;
}
