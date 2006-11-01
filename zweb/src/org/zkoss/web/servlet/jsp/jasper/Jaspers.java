/* Jaspers.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Apr  8 11:09:32     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.jsp.jasper;

import javax.servlet.jsp.PageContext;
import org.apache.jasper.compiler.JspUtil;

/**
 * Utilities to access Jasper (Tomcat's JSP engine).
 *
 * @author tomyeh
 */
public class Jaspers {
	/** Maps a JSP URI to a Java name.
	 * @param pkg the package name (such ash "org.zkoss.jsp")
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
