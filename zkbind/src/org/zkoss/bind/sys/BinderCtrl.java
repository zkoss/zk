/* BinderCtrl.java

	Purpose:
		
	Description:
		
	History:
		2011/11/7 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys;

import java.util.List;
import java.util.Set;

import org.zkoss.bind.Binder;
import org.zkoss.bind.Form;
import org.zkoss.bind.sys.tracker.Tracker;
import org.zkoss.zk.ui.Component;


/**
 * An addition interface to {@link Binder}
 * that is used for implementation or tools. <br/>
 *  <br/>
 * Application developers rarely need to access methods in this interface.
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
	
	/**
	 * Get the {@link ValidationMessages}
	 * @return null if no one set the instance by {@link #setValidationMessages(ValidationMessages)}
	 */
	public ValidationMessages getValidationMessages();
	
	/**
	 * Set the {@link ValidationMessages}
	 * @param messages the {@link ValidationMessages}
	 */
	public void setValidationMessages(ValidationMessages messages);
	
	/**
	 * is there a validator on the attribute of component
	 * @param comp the component to check
	 * @param attr the attribute to check
	 * @return true if there is a validator
	 */
	public boolean hasValidator(Component comp, String attr);
	
	/**
	 * get the template resolver that sets by {@link Binder#setTemplate(Component, String, String, java.util.Map)}
	 * @param comp the component has resolvers
	 * @param attr the attribute to get the resolver
	 * @return the resolver, null if not existed.
	 */
	public TemplateResolver getTemplateResolver(Component comp, String attr);


	/**
	 * get all load prompt binding of the component and attribute
	 * @param comp the component is relative to the bindings
	 * @param attr the attribute is relative to the bindings
	 * @return
	 */
	public List<Binding> getLoadPromptBindings(Component comp, String attr);
	
}
