/* FormBean.java

	Purpose:
		
	Description:
		
	History:
		Jun 24, 2011 6:36:05 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

import java.util.Set;

/**
 * Virtual Bean that associated with a form. 
 * @author henrichen
 * @since 6.0.0
 */
public interface Form {
//	/**
//	 * Returns the id of this Form.
//	 * @return the id of this Form.
//	 */
//	public String getId();
	
	/**
	 * Returns all field names that this Form bean is care about for both read and load.
	 * @return all field names that this Form bean is care about.
	 */
	public Set<String> getFieldNames();

	/**
	 * Sets the associated value of the specified field name.
	 * @param field field name
	 * @param value the associated value
	 */
	public void setField(String field, Object value);
	
	/**
	 * Returns the associated value of the specified field name.
	 * @param field field name
	 * @return the associated value
	 */
	public Object getField(String field);
	
	
	/**
	 * Returns whether the form has been modified (Any field is different from those of loadBean) 
	 * @return whether the form has been modified.
	 */
	public boolean isDirty();
}
