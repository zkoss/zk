/* Jaspers.java

{{IS_NOTE
	$Id: Jaspers.java,v 1.4 2006/02/27 03:54:33 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Fri Apr  8 11:09:32     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.servlet.jsp.jasper;

import javax.servlet.jsp.PageContext;
import org.apache.jasper.compiler.JspUtil;

/**
 * Utilities to access Jasper (Tomcat's JSP engine).
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.4 $ $Date: 2006/02/27 03:54:33 $
 */
public class Jaspers {
	/** Maps a JSP URI to a Java name.
	 * @param pkg the package name (such ash "com.potix.jsp")
	 */
	public static String mapJspToJavaName(String pkg, String jspURI) {
		final StringBuffer javaName =
			new StringBuffer(128).append(pkg).append('.');
		final int j = jspURI.lastIndexOf('/');
        if (j > 0)
        	javaName
        		.append(JspUtil.makeJavaPackage(jspURI.substring(1, j)))
        		.append('.');
		return javaName
			.append(JspUtil.makeJavaIdentifier(jspURI.substring(j + 1)))
			.toString();
	}
}
