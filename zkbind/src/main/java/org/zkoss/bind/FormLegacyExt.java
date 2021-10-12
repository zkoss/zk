/* FormLegacyExt.java

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
 * @deprecated As of release 9.5.0, please use {@link org.zkoss.bind.Form}
 * @author Leon
 * @since 9.5.0
 */
@Deprecated
public interface FormLegacyExt {
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
