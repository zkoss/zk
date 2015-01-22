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
import org.zkoss.bind.PhaseListener;
import org.zkoss.bind.sys.debugger.BindingAnnotationInfoChecker;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.bind.sys.tracker.Tracker;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventQueues;


/**
 * An addition interface to {@link Binder}
 * that is used for implementation or tools. <br/>
 *  <br/>
 * Application developers rarely need to access methods in this interface.
 * @author dennis
 * @author jumperchen
 * @since 6.0.0
 */
public interface BinderCtrl {	
	/**
	 * Default queue name of a binder to share the bean notification and global commands
	 */
	public static final String DEFAULT_QUEUE_NAME = "$ZKBIND_DEFQUE$"; //the associated event queue name
	
	/**
	 * Default queue scope of a binder to share the bean notification and global commands
	 */
	public static final String DEFAULT_QUEUE_SCOPE = EventQueues.DESKTOP; //the associated event queue name
	
	/**
	 * PhaseListener key
	 */
	public static final String PHASE_LISTENER_CLASS_KEY = "org.zkoss.bind.PhaseListener.class";
	
	//control keys
	public static final String BINDING = "$BINDING$"; //a binding
	public static final String BINDER = "$BINDER$"; //the binder
	public static final String BINDCTX = "$BINDCTX$"; //bind context
	public static final String VAR = "$VAR$"; //variable name in a collection
	public static final String VM = "$VM$"; //the associated view model
	public static final String NOTIFYS = "$NOTIFYS$"; //changed properties to be notified
	public static final String VALIDATES = "$VALIDATES$"; //properties to be validated
	public static final String SRCPATH = "$SRCPATH$"; //source path that trigger @DependsOn tracking
	public static final String DEPENDS_ON_COMP = "$DEPENDS_ON_COMP"; //dependsOn component
	public static final String RENDERER_INSTALLED = "$RENDERER_INSTALLED$";

	public static final String LOAD_FORM_EXPRESSION = "$LOAD_FORM_EXPR$";//The attribute name of a loaded bean class, internal use only
	public static final String LOAD_FORM_COMPONENT = "$LOAD_FORM_COMP$";//The attribute name of a loaded bean class, internal use only
	
	public static final String IGNORE_TRACKER = "$IGNORE_TRACKER$"; //ignore adding currently binding to tracker, ex in init

	public static final String IGNORE_REF_VALUE = "$IGNORE_REF_VALUE$"; //ignore getting nested value form ref-binding when doing el evaluation.
	public static final String INVALIDATE_REF_VALUE = "$INVALIDATE_REF_VALUE$"; //invalidate getting nested value form ref-binding when doing el evaluation.

	public static final String SAVE_BASE = "$SAVE_BASE$"; //bean base of a save operation
	public static final String ON_BIND_INIT = "onBindInit"; //do component binding initialization
	public static final String ON_BIND_CLEAN = "onBindClean"; //do component binding clean up
	public static final String MODEL = "$MODEL$"; //collection model for index tracking

	//private control key
	public static final String FORM_ID = "$FORM_ID$";
	public static final String CHILDREN_ATTR = "$CHILDREN$";
	public static final String ACTIVATOR = "$ACTIVATOR$";//the activator that is stored in root comp
	
	//ZK-2545 - Children binding support list model
	public static final String CHILDREN_BINDING_RENDERED_COMPONENTS = "$CHILDREN_BINDING_RENDERED_COMPONENTS$";
	
	/**
	 * Add a association between formId and a associated save binding(save binding inside a form), the form has to exist in the parent components
	 * @param associatedComp associated component inside a form binding
	 * @param formId the form id
	 * @param saveBinding the nested save binding in side a form binding
	 * @param fieldName the associated form fieldName for the associated save binding
	 * @since 6.0.1
	 */
	public void addFormAssociatedSaveBinding(Component associatedComp, String formId, SaveBinding saveBinding, String fieldName);
	
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
	 * @return the prompt-load-bindings
	 */
	public List<Binding> getLoadPromptBindings(Component comp, String attr);
	
	
	/**
	 * get the {@link PhaseListener}
	 * @return the {@link PhaseListener}
	 */
	public PhaseListener getPhaseListener();
	
	/**
	 * set the {@link PhaseListener}
	 * @param listener the {@link PhaseListener}
	 */
	public void setPhaseListener(PhaseListener listener);
	
	/**
	 * check if binder is in activating state
	 * @return true if binder is currently in activating state
	 * @since 6.0.1
	 */
	public boolean isActivating();

	/** 
	 * get binding execution info collector
	 * @return the collector instance or null if no collector is existed
	 * @since 6.5.2 
	 */
	public BindingExecutionInfoCollector getBindingExecutionInfoCollector();
	
	/** 
	 * get binding annotation info checker
	 * @return the collector instance or null if no collector is existed
	 * @since 6.5.2 
	 */
	public BindingAnnotationInfoChecker getBindingAnnotationInfoChecker();
	
	/**
	 * Returns the queue name of this binder
	 * @since 8.0.0
	 */
	public String getQueueName();

	/**
	 * Returns the queue scope of this binder
	 * @since 8.0.0
	 */
	public String getQueueScope();
	
	/**
	 * Adds a field name for saving with the given Form.
	 * @param fieldName field name to be saved into.
	 * @since 8.0.0
	 */
	public void addSaveFormFieldName(Form form, String fieldName);
	
	/**
	 * Adds all field names for saving with the given Form.
	 * @param fieldName field name to be saved into.
	 * @since 8.0.0
	 */
	public void addSaveFormFieldName(Form form, Set<String> fieldNames);

	/**
	 * Returns all field names for saving with the given Form.
	 * @param fieldName field name to be saved into.
	 * @since 8.0.0
	 */
	public Set<String> getSaveFormFieldNames(Form self);

	/**
	 * Remove all field names for saving with the given Form.
	 * @param fieldName field name to be saved into.
	 * @since 8.0.0
	 */
	public Set<String> removeSaveFormFieldNames(Form self);
}
