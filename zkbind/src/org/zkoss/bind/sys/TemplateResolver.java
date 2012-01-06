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
 *
 */
public interface TemplateResolver {
	public static final String EACH_ATTR = "var";
	public static final String EACH_VAR = "each";
	public static final String STATUS_ATTR = "status";
	public static final String STATUS_POST_VAR = "Status";
	public static final String EACH_STATUS_VAR = EACH_VAR + STATUS_POST_VAR;
	
	
	Template resolveTemplate(Component eachComp, Object eachData, int index);
	
	void addTemplateDependency(Component eachComp);
}
