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
 *
 */
public interface Form {
//	/**
//	 * Returns the id of this Form.
//	 * @return the id of this Form.
//	 */
//	public String getId();
	
	/**
	 * Returns all field names taht this Form bean is care about for both read and load.
	 * @return all field names taht this Form bean is care about.
	 */
	public Set<String> getFieldNames();
	
	/**
	 * Add a field name for loading from this Form.
	 * @param fieldName field name to be loaded from.
	 */
	public void addLoadFieldName(String fieldName);
	
	/**
	 * Add a field name for saving into this Form.
	 * @param fieldName field name to be saved into.
	 */
	public void addSaveFieldName(String fieldName);
	
	/**
	 * Returns all field names that this Form bean is care about for loading value from the real bean.  
	 * @return all field names that this Form bean is care about for loading value from the real bean.
	 */
	public Set<String> getLoadFieldNames();

	/**
	 * Returns all those field name that this Form bean is care about for saving value into the real bean.
	 * @return all those field name that this Form bean is care about for saving value into the real bean.
	 */
	public Set<String> getSaveFieldNames();

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
