/* Tabs.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 12 10:43:14     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;

import com.potix.zul.html.impl.XulElement;

/**
 * A collection of tabs ({@link Tab}).
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Tabs extends XulElement {
	/** Returns the tabbox owns this component.
	 */
	public Tabbox getTabbox() {
		return (Tabbox)getParent();
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Tabbox))
			throw new UiException("Wrong parent: "+parent);

		final Tabbox oldp = (Tabbox)getParent();
		super.setParent(parent);

		invalidateIfAccordion(oldp);
		invalidateIfAccordion((Tabbox)parent);
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Tab))
			throw new UiException("Unsupported child for tabs: "+child);

		final boolean empty = getChildren().isEmpty();
		final Tab newtab = (Tab)child;
		if (!empty && newtab.isSelected()) newtab.setSelectedDirectly(false);

		if (super.insertBefore(child, insertBefore)) {
			final Tabbox tabbox = getTabbox();

			if (empty && tabbox != null)
				tabbox.setSelectedTab(newtab);

			invalidateIfAccordion(tabbox);
			return true;
		}
		return false;
	}
	/** Invalidates the tabbox if it is accordion.
	 */
	private static void invalidateIfAccordion(Tabbox tabbox) {
		if (tabbox != null && "accordion".equals(tabbox.getMold()))
			tabbox.invalidate(INNER);
	}
}
