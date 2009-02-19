/* Tabpanel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 12 10:43:10     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Iterator;

import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;

import org.zkoss.zul.impl.XulElement;

/**
 * A tab panel.
 * <p>Default {@link #getZclass}: z-tabpanel. (since 3.5.0)
 *
 * @author tomyeh
 */
public class Tabpanel extends XulElement implements org.zkoss.zul.api.Tabpanel {
	public Tabpanel() {
	}

	/** Returns the tabbox owns this component.
	 */
	public Tabbox getTabbox() {
		final Tabpanels panels = (Tabpanels)getParent();
		return panels != null ? panels.getTabbox(): null;
	}
	/**
	 * Returns the tabbox owns this component.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Tabbox getTabboxApi() {
		return getTabbox();
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
	/** Returns the tab associated with this tab panel.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Tab getLinkedTabApi() {
		return getLinkedTab();
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
	/** Returns the style class.
	 *
	 * @since 3.5.0
	 */
	public String getZclass() {
		if (_zclass != null) return _zclass;
		final Tabbox tabbox = getTabbox();
		final String added = tabbox != null ? tabbox.inAccordionMold() ? "-" + tabbox.getMold() :
				tabbox.isVertical() ? "-ver" : "" : "";
		return "z-tabpanel" + added;
	}

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
