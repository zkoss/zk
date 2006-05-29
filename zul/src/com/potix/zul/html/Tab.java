/* Tab.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 12 10:43:18     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.Iterator;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;

import com.potix.zul.html.impl.LabelImageElement;

/**
 * A tab.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Tab extends LabelImageElement {
	private boolean _selected;

	public Tab() {
	}
	public Tab(String label) {
		setLabel(label);
	}
	public Tab(String label, String image) {
		setLabel(label);
		setImage(image);
	}

	/** Returns the tabbox owns this component.
	 */
	public Tabbox getTabbox() {
		final Tabs tabs = (Tabs)getParent();
		return tabs != null ? tabs.getTabbox(): null;
	}
	/** Returns the panel associated with this tab.
	 */
	public Tabpanel getLinkedPanel() {
		final int j = getIndex();
		if (j >= 0) {
			final Tabbox tabbox = getTabbox();
			if (tabbox != null) {
				final Tabpanels tabpanels = tabbox.getTabpanels();
				if (tabpanels != null && tabpanels.getChildren().size() > j)
					return (Tabpanel)tabpanels.getChildren().get(j);
			}
		}
		return null;
	}

	/** Returns whether this tab is selected.
	 */
	public final boolean isSelected() {
		return _selected;
	}
	/** Sets whether this tab is selected.
	 */
	public void setSelected(boolean selected) {
		if (_selected != selected) {
			final Tabbox tabbox = (Tabbox)getTabbox();
			if (tabbox != null) {
				//Note: we don't update it here but let its parent does the job
				tabbox.setSelectedTab(this);
			} else {
				_selected = selected;
				invalidate(INNER);
			}
		}
	}
	/** Updates _selected directly without updating the client.
	 */
	/*package*/ void setSelectedDirectly(boolean selected) {
		_selected = selected;
	}

	/** Returns the index of this panel, or -1 if it doesn't belong to any
	 * tabs.
	 */
	public int getIndex() {
		final Tabs tabs = (Tabs)getParent();
		if (tabs == null)
			return -1;
		int j = 0;
		for (Iterator it = tabs.getChildren().iterator();; ++j)
			if (it.next() == this)
				return j;
	}

	//-- super --//
	/** Returns the style class.
	 * Note: 1) if not specified (or setSclass(null)), "tab" is assumed;
	 * 2) if selected, it appends "sel" to super's getSclass().
	 */
	public String getSclass() {
		String scls = super.getSclass();
		if (scls == null) scls = "tab";
		return isSelected() ? scls + "sel": scls;
	}

	//-- Component --//
	public void invalidate(Range range) {
		final Tabbox tabbox = getTabbox();
		if (tabbox != null && "accordion".equals(tabbox.getMold()))
			tabbox.invalidate(INNER);
		else
			super.invalidate(INNER);
	}
	public boolean isChildable() {
		return false;
	}
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Tabs))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
 }
