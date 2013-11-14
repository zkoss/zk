/* TemplateResolver.java

	Purpose:
		
	Description:
		
	History:
		2012/1/4 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Template;


/**
 * Resolver for Template
 * @author dennis
 * @since 6.0.0
 */
public interface TemplateResolver {
	public static final String EACH_ATTR = "var";
	public static final String EACH_VAR = "each";
	public static final String STATUS_ATTR = "status";
	public static final String EACH_STATUS_VAR = "forEachStatus";
	
	//ZK-1787When the viewModel tell binder to reload a list, the other component that bind a bean in the list will reload again
	public static final String TEMPLATE_OBJECT = "$TemplateVar$";
	
	/**
	 * Resolve the template for the component 
	 * @param eachComp the template to be resolved of the component
	 * @param eachData the data for resolver
	 * @param index the index of each
	 * @param size the size of data set
	 * @param subType the sub-type of template should be resolved
	 * @return the template if exist.
	 * @since 7.0.0
	 */
	Template resolveTemplate(Component eachComp, Object eachData, int index, int size, String subType);
	
	/**
	 * @deprecated since 7.0.0
	 * use {@link #resolveTemplate(Component, Object, int, int, String)}
	 */
	@Deprecated
	Template resolveTemplate(Component eachComp, Object eachData, int index, int size);
	
	/**
	 * Add template tracking to component
	 * @param eachComp the component to add template tracking
	 * @deprecated since 6.5.3
	 */
	@Deprecated
	void addTemplateTracking(Component eachComp);
	
	/**
	 * Add template tracking to component
	 * @param eachComp the template to be resolved of the component
	 * @param eachData the data for resolver
	 * @param index the index of each
	 * @param size the size of data set
	 */
	void addTemplateTracking(Component eachComp, Object eachData, int index, int size);
}
