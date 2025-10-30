/** FormProxyObject.java.

	Purpose:
		
	Description:
		
	History:
		12:04:46 PM Dec 25, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.proxy;

import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.sys.FormBinding;
import org.zkoss.bind.sys.SavePropertyBinding;
import org.zkoss.util.Pair;

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

	/**
	 * Set the property of current form proxy object and it's creator
	 * @param property p the property
	 * @param parent parent the parent proxy node
	 * @since 8.0.2
	 */
	public void setPath(String property, ProxyNode parent);

	/**
	 * Cache save property binding by property string
	 * @param property the property of save property binding
	 * @param savePropertyBinding the save property binding
	 * @since 8.0.2
	 */
	public void cacheSavePropertyBinding(String property, SavePropertyBinding savePropertyBinding);

	/**
	 * Collect all of collect cached save property bindings
	 * @since 8.0.2
	 */
	public Set<Pair<String, SavePropertyBinding>> collectCachedSavePropertyBinding();
}
