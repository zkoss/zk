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
	
	Template resolveTemplate(Component eachComp, Object eachData, int index);
}
