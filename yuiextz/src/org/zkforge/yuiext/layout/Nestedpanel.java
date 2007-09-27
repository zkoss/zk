/* Nestedpanel.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Aug 7, 2007 6:32:26 PM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkforge.yuiext.layout;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * Create a new NestedLayoutPanel. This class refer to Ext.NestedLayoutPanel by
 * Ext JS at client side.
 * 
 * @author jumperchen
 * 
 */
public class Nestedpanel extends Contentpanel {
	
	public Borderlayout getLayout() {
		if (!getChildren().isEmpty())
			return (Borderlayout) getChildren().get(0);
		return null;
	}

	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Borderlayout))
			throw new UiException("Unsupported child for Nestedpanel: " + child);
		if (getChildren().size() > 0)
			throw new UiException("Only one child is allowed: " + this);
		final boolean result =  super.insertBefore(child, insertBefore);
		if(result)invalidate();
		return result;
	}

}
