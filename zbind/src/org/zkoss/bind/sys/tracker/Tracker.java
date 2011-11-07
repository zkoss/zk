/* BindTracker.java

	Purpose:
		
	Description:
		
	History:
		Jun 29, 2011 6:33:22 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.tracker;

import java.util.Set;

import org.zkoss.bind.Form;
import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.LoadBinding;
import org.zkoss.bind.sys.SaveBinding;
import org.zkoss.zk.ui.Component;

/**
 * Bind tracker to maintain a binding dependency graph.
 * e.g. @{a.b.c, save-onchange:d.e.f} then it forms a dependency from d.e.f to a.b.c for save. 
 * @author henrichen
 */
public interface Tracker {
	/**
	 * Add a tracking that associate a binding to a dot series under the specified {@link Component}
	 * @param comp the associated component
	 * @param series the dot series as an array of property name
	 * @param dependentpath the dot series as an array of property name of the dependent property 
	 * @param binding the binding
	 */
	public void addTracking(Component comp, String[] series, String[] dependentpath, Binding binding);
	
	/**
	 * Remove all tracking associated with the specified {@link Component}. 
	 * @param comp the associated component
	 */
	public void removeTrackings(Component comp);

	/**
	 * Tie a property to its corresponding value under the specified {@link Component}.
	 * @param comp the associated component
	 * @param base the base object of the property
	 * @param script the field script
	 * @param propName the resolved property name from the field script
	 * @param value the value of the property
	 */
	public void tieValue(Object comp, Object base, Object script, Object propName, Object value);
	
	/**
	 * Returns all bindings that associated with the specified property.
	 * @param base base object
	 * @param propName property name
	 * @return all LoadBindings that associated with the specified property.
	 */
	public Set<LoadBinding> getLoadBindings(Object base, String propName);
	
	
	/**
	 * Add a tracking between formId and a associated save binding(save binding inside a form), the form has to exist in the parent components
	 * @param associatedComp associated component inside a form binding
	 * @param formId the form id
	 * @param saveBinding the nested save binding in side a form binding
	 */
	public void addFormSaveBindingTracking(Component associatedComp, String formId, SaveBinding saveBinding);
	
	/**
	 * Get the tracked associated save bindings of a form in a component
	 * @param formComp the component that contains the form
	 * @param formId the form id
	 * @return all nested save binding in the form 
	 */
	public Set<SaveBinding> getFormSaveBinding(Component formComp, String formId);
	
}
