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
	public static final String STATUS_POST_VAR = "Status";
	public static final String EACH_STATUS_VAR = EACH_VAR + STATUS_POST_VAR;
	
	/**
	 * Resolve the template for the component 
	 * @param eachComp the template to be resolved of the component
	 * @param eachData the data for resolver
	 * @param index the index for resolver
	 * @return the template if exist.
	 */
	Template resolveTemplate(Component eachComp, Object eachData, int index);
	
	/**
	 * Add template tracking to component
	 * @param eachComp the component to add template tracking
	 */
	void addTemplateTracking(Component eachComp);
}
