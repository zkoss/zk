/* Tabpanel.java

{{IS_NOTE
	$Id: Tabpanel.java,v 1.5 2006/05/04 11:21:07 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Jul 12 10:43:10     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.Iterator;

import com.potix.xml.HTMLs;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;

import com.potix.zul.html.impl.XulElement;

/**
 * A tab panel.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.5 $ $Date: 2006/05/04 11:21:07 $
 */
public class Tabpanel extends XulElement {
	public Tabpanel() {
	}

	/** Returns the tabbox owns this component.
	 */
	public Tabbox getTabbox() {
		final Tabpanels panels = (Tabpanels)getParent();
		return panels != null ? panels.getTabbox(): null;
	}
	/** Returns the tab associated with this tab panel.
	 */
	public Tab getLinkedTab() {
		final int j = getIndex();
		if (j >= 0) {
			final Tabbox tabbox = getTabbox();
			if (tabbox != null) {
				final Tabs tabs = tabbox.getTabs();
				if (tabs != null && tabs.getChildren().size() > j)
					return (Tab)tabs.getChildren().get(j);
			}
		}
		return null;
	}
	/** Returns whether this tab panel is selected.
	 */
	public boolean isSelected() {
		final Tab tab = getLinkedTab();
		return tab != null && tab.isSelected();
	}

	/** Returns the index of this panel, or -1 if it doesn't belong to any
	 * tabpanels.
	 */
	public int getIndex() {
		final Tabpanels tabpanels = (Tabpanels)getParent();
		if (tabpanels == null)
			return -1;
		int j = 0;
		for (Iterator it = tabpanels.getChildren().iterator();; ++j)
			if (it.next() == this)
				return j;
	}

	//-- super --//

	//-- Component --//
	public boolean isVisible() {
		return super.isVisible() && isSelected();
	}
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Tabpanels))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
}
