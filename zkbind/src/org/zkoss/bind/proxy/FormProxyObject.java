/** FormProxyObject.java.

	Purpose:
		
	Description:
		
	History:
		12:04:46 PM Dec 25, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.proxy;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.sys.FormBinding;

/**
 * The interface implemented by proxy classes.
 * <p>Note: To avoid the method naming conflict with the origin object, we declare
 * those long method names, <tt>getOriginObject</tt>, <tt>resetFormOrigin</tt>,
 * <tt>submitToOrigin</tt>, and <tt>isDirtyForm</tt>
 * @author jumperchen
 * @since 8.0.0
 */
public interface FormProxyObject {
	/**
	 * Returns the origin object.
	 */
	public Object getOriginObject();
	/**
	 * Resets all of the changes of this form from the origin object.
	 */
	public void resetFromOrigin();
	/**
	 * Saves all of the changes of this form to the origin object.
	 * @param ctx
	 */
	public void submitToOrigin(BindContext ctx);
	/**
	 * Returns whether the form proxy object is dirty or not, including all
	 * of its attributes.
	 */
	public boolean isFormDirty();
	
	/**
	 * Sets the owner of this form with its binding.
	 * @param owner the object associated with this form.
	 */
	public void setFormOwner(Object owner, FormBinding binding);
}
