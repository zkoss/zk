/* BranchComponent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 7, 2007 5:56:04 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsf.zul.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * The skeletal class used to implement the ZULJSF components
 * that might have child ZULJSF components.
 * This component should be declared nested under {@link org.zkoss.jsf.zul.Page}.
 * @author Dennis.Chen
 */
abstract public class BranchComponent extends LeafComponent{

	/** Adds a child ZUL Component.
	 */
	/*package*/ void addChildZULComponent(LeafComponent child) {
		child.getZULComponent().setParent(getZULComponent());
	}
	
	/**
	 * Call by RootComponent or BranchComponent to load zk stuff and all it's children
	 */
	protected void loadZULTree(StringWriter writer) throws IOException{
		if (!isRendered() || !isEffective())
			return; //nothing to do
		initComponent();
		
		//load children
		ComponentInfo ci = getComponentInfo();
		List children = ci.getChildrenInfo(this);
		if(children!=null){
			for (Iterator kids = children.iterator(); kids.hasNext(); ){
				AbstractComponent kid = (AbstractComponent) kids.next();
	            kid.loadZULTree(writer);
	        }
			String bodyContent = getBodyContent();
			Utils.adjustChildren(
					null, this, ci.getChildrenInfo(this), bodyContent);
		}else{
			//bug #1832862 Content disappear in JSFComponent
			Utils.adjustChildren(
					null, this, new ArrayList(), getBodyContent());
		}
		afterComposeComponent();// after Compose Component.
		setBodyContent(null); //clear
		
		
		
	}
	


}
