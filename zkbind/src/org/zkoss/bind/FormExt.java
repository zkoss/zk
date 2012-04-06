/* FormExt.java

	Purpose:
		
	Description:
		
	History:
		2011/12/1 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

import java.util.Set;



/**
 * An addition interface of {@link Form} to provide
 * more control.
 * @author dennis
 * @since 6.0.0
 */
public interface FormExt {

	/**
	 * @return the status object of this form
	 */
	public FormStatus getStatus();
	
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
	 * Reset the dirty data, reload initValue value from field
	 */
	public void resetDirty();
	
}
