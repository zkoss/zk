/* BranchTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 20 17:09:09     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsp.zul.impl;

import java.io.StringWriter;
import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.zkoss.zk.ui.Component;

/**
 * The skeletal class used to implement the JSP tag for ZK components
 * that might have child tags (and/or components).
 *
 * <p>Remember to declare the following in the TLD file:
 * &lt;body-content&gt;scriptless&lt;/body-content&gt;
 *
 * @author tomyeh
 */
abstract public class BranchTag extends LeafTag {
	/** Adds a child tag.
	 */
	public void addChildTag(ComponentTag child) {
		if(child.isInlineMacro())
		{
			Component[] comps = child.getComponents();
			for(int i=0;i<comps.length;i++)
				comps[i].setParent(getComponent());
		}
		else child.getComponent().setParent(getComponent());
	}


	/** To process this page tag.
	 * The deriving class rarely need to override this method.
	 */
	public void doTag() throws JspException, IOException {
		if (!isEffective())
			return; //nothing to do

		initComponent(); //creates and registers component

		final StringWriter out = new StringWriter();
		if(getJspBody()!=null)getJspBody().invoke(out);
		if(isInlineMacro())
		{
			Component[] comps = getComponents();
			for(int i=0;i<comps.length;i++)
				Utils.adjustChildren(
						null, comps[i], comps[i].getChildren(), out.toString());
		}
		else
		{
			Component comp = getComponent();
			Utils.adjustChildren(
				null, comp, comp.getChildren(), out.toString());	
		}
		afterComposeComponent();// after Compose Component.
		writeComponentMark(); //write a special mark
	}
	
}
