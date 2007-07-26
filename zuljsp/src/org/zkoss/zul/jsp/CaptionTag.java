/* CaptionTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 26, 2007 10:57:06 AM 2007, Created by Ian Tsai
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.jsp;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Caption;
import org.zkoss.zul.jsp.impl.BranchTag;

/**
 * @author ian
 *
 */
public class CaptionTag extends BranchTag {

	/**
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * 
	 */
	protected Component newComponent(Class use) 
	throws InstantiationException, IllegalAccessException{
		//System.out.println("new Caption()!!!");
		return (Component) (use==null?new Caption():use.newInstance());
	}

}
