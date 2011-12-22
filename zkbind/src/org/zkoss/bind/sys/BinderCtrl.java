/* BinderCtrl.java

	Purpose:
		
	Description:
		
	History:
		2011/11/7 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys;

import java.util.Set;

import org.zkoss.bind.Binder;
import org.zkoss.bind.Form;
import org.zkoss.bind.sys.tracker.Tracker;
import org.zkoss.zk.ui.Component;


/**
 * An addition interface to {@link Binder}
 * that is used for implementation or tools.
 *
 * <p>Application developers rarely need to access methods in this interface.
 * @author dennis
 *
 */
public interface BinderCtrl {	
	
	/**
	 * Add a association between formId and a associated save binding(save binding inside a form), the form has to exist in the parent components
	 * @param associatedComp associated component inside a form binding
	 * @param formId the form id
	 * @param saveBinding the nested save binding in side a form binding
	 */
	public void addFormAssociatedSaveBinding(Component associatedComp, String formId, SaveBinding saveBinding);
	
	/**
	 * Get associated save bindings of a form in a component
	 * @param formComp the component that contains the form
	 * @return all associated save binding in the form 
	 */
	public Set<SaveBinding> getFormAssociatedSaveBindings(Component formComp);
	
	/**
	 * Store the form in the component with id
	 * @param comp the component to store the form
	 * @param id the form id
	 * @param form the form instance
	 */
	public void storeForm(Component comp,String id, Form form);
	
	/**
	 * Get the form of the component
	 * @param comp the component has the form
	 * @param id the form id
	 * @return the form if there is a form inside the component with the id
	 */
	public Form getForm(Component comp,String id);
	
	/**
	 * Returns associated dependency tracker of this binder.
	 * @return associated dependency tracker of this binder.
	 */
	public Tracker getTracker();
	
}
