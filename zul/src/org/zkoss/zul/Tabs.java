/* Tabs.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 12 10:43:14     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;

import org.zkoss.zul.impl.XulElement;

/**
 * A collection of tabs ({@link Tab}).
 *
 * @author tomyeh
 */
public class Tabs extends XulElement {
	/** Returns the tabbox owns this component.
	 * <p>It is the same as {@link #getParent}.
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

		boolean sel = getChildren().isEmpty(), desel = false;
		final Tab newtab = (Tab)child;
		if (!sel && newtab.isSelected()) {
			newtab.setSelectedDirectly(false);	//turn off first
			sel = desel = true;					//trun on later
		}

		if (super.insertBefore(child, insertBefore)) {
			final Tabbox tabbox = getTabbox();

			if (sel)
				if (tabbox != null) {
					tabbox.setSelectedTab(newtab);
				} else {
					newtab.setSelectedDirectly(true);
					if (desel)
						for (Iterator it = getChildren().iterator(); it.hasNext();) {
							final Tab tab = (Tab)it.next();
							if (tab != newtab && tab.isSelected()) {
								tab.setSelectedDirectly(false);
								break;
							}
						}
				}

			invalidateIfAccordion(tabbox);
			return true;
		}
		return false;
	}
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);

		final Tabbox tabbox = getTabbox();
		if (tabbox != null)
			((Tab)child).removeEventListener(Events.ON_SELECT, tabbox._listener);

		if (tabbox == null || !tabbox.inAccordionMold())
			smartUpdate("z.init", true); //fixWidth
	}
	public void onChildAdded(Component child) {
		super.onChildAdded(child);

		final Tabbox tabbox = getTabbox();
		if (tabbox != null)
			((Tab)child).addEventListener(Events.ON_SELECT, tabbox._listener);

		if (tabbox == null || !tabbox.inAccordionMold())
			smartUpdate("z.init", true); //fixWidth
	}
		
	/** Invalidates the tabbox if it is accordion.
	 */
	private static void invalidateIfAccordion(Tabbox tabbox) {
		if (tabbox != null && tabbox.inAccordionMold())
			tabbox.invalidate();
	}
}
