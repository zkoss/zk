/** FormCtrl.java.

	Purpose:
		
	Description:
		
	History:
		6:22:32 PM Dec 23, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

import java.util.Set;

import org.zkoss.bind.sys.FormBinding;

/**
 * An extra control API for {@link Form}
 * @author jumperchen
 * @since 8.0.0
 */
public interface FormCtrl {

	/**
	 * Returns the status object of this form
	 */
	public FormStatus getStatus();
	
	/**
	 * Adds a field name for saving into this Form.
	 * @param fieldName field name to be saved into.
	 */
	public void addSaveFieldName(String fieldName);

	/**
	 * Returns all those field name that this Form bean is care about for saving value into the real bean.
	 */
	public Set<String> getSaveFieldNames();
	
	/**
	 * Reset the dirty data, reload initValue value from field
	 */
	public void resetDirty();
	
	/**
	 * Sets the owner of this form with its binding.
	 * @param owner the object associated with this form.
	 */
	public void setOwner(Object owner, FormBinding binding);

	/**
	 * Replaces the form from the give new form.
	 */
	public void replaceForm(FormCtrl newForm);
}
