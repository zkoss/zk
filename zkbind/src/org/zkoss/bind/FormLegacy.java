/* FormLegacy.java

		Purpose:
		
		Description:
		
		History:
				Wed Feb 19 15:45:29 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

import java.util.Set;

/**
 * For compatibility only.
 * @deprecated As of release 9.1.0, please use {@link org.zkoss.bind.Form}
 * @author Leon
 * @since 9.1.0
 */
@Deprecated
public interface FormLegacy extends Form {
	/**
	 * Returns all field names that this FormLegacy bean is care about for both read and load.
	 * @return all field names that this FormLegacy bean is care about.
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