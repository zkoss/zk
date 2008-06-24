/* ColumnChildren.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun  4 17:45:11 TST 2008, Created by gracelin
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zkmax.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.XulElement;

/**
 * The column of Columnlayout
 * 
 * @author gracelin
 * @since 3.5.0
 */
public class Columnchildren extends XulElement {

	public Columnchildren() {
		super();
		setSclass("z-column-children");
	}

	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Columnlayout))
			throw new UiException("Wrong parent: " + parent);
		super.setParent(parent);
	}
}
