/* Tabpanels.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 12 10:43:08     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

import org.zkoss.zul.impl.XulElement;

/**
 * A collection of tab panels.
 *
 * <p>Default {@link #getSclass}: tabpanels.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Tabpanels extends XulElement {
	public Tabpanels() {
		setSclass("tabpanels");
	}

	/** Returns the tabbox owns this component.
	 */
	public Tabbox getTabbox() {
		return (Tabbox)getParent();
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Tabbox))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Tabpanel))
			throw new UiException("Unsupported child for tabpanels: "+child);
		return super.insertBefore(child, insertBefore);
	}
}
