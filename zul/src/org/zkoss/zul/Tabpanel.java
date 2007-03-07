/* Tabpanel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 12 10:43:10     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Iterator;

import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

import org.zkoss.zul.impl.XulElement;

/**
 * A tab panel.
 *
 * <p>Default {@link #getSclass}: tabpanel if the default mold is used
 * for the tabbox ({@link #getTabbox}),
 * or tabpanel-<em>mold</em> if other mold is used (where mold is the value
 * returned by {@link #getMold}.
 *
 * @author tomyeh
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
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		final String clkattrs = getAllOnClickAttrs(false);
		return clkattrs == null ? attrs: attrs + clkattrs;
	}
	/** Returns the style class.
	 *
	 * <p>The default style class, i.e., the style class is not defined (i.e.,
	 * {@link #setSclass} is not called or called with null or empty):
	 * it returns "tabpanel" if {@link #getMold} of {@link #getTabbox}
	 * returns "default",
	 * or "tabpanel-<em>mode</em>" if other mold is used,
	 * where <em>mode</em> is the value returned by {@link #getMold}.
	 */
	public String getSclass() {
		final String scls = super.getSclass();
		if (scls != null) return scls;

		final Tabbox tabbox = getTabbox();
		final String mold = tabbox != null ? tabbox.getMold(): null;
		return mold == null || "default".equals(mold) ?
			"tabpanel": "tabpanel-"+mold;
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
