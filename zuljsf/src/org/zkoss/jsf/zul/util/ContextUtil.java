/* ContextUtil.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2007/8/22 11:16:22 AM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsf.zul.util;

import javax.faces.context.FacesContext;

/**
 * a utitlity for JSF context
 * @author Dennis.Chen
 */
public class ContextUtil {
	
	/**
	 * get Bean from current faces context;
	 * @param name bean name
	 * @return instance of bean by bean name.
	 */
	public static Object getBean(String name){
        FacesContext context = FacesContext.getCurrentInstance();
        Object obj = context.getApplication().getVariableResolver().resolveVariable(context,name);
        return obj;
    }
}
