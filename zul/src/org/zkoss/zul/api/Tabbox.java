/* Tabbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import org.zkoss.zk.ui.WrongValueException;

;

/**
 * A tabbox.
 * 
 * <p>
 * Event:
 * <ol>
 * <li>org.zkoss.zk.ui.event.SelectEvent is sent when user changes the tab.</li>
 * </ol>
 * 
 * <p>
 * Mold:
 * <dl>
 * <dt>default</dt>
 * <dd>The default tabbox.</dd>
 * <dt>accordion</dt>
 * <dd>The accordion tabbox.</dd>
 * </dl>
 * 
 * <p>
 * <p>
 * Default {@link #getZclass}: z-tabbox. (since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Tabbox extends org.zkoss.zul.impl.api.XulElement {

	/**
	 * Returns the tabs that this tabbox owns.
	 */
	public org.zkoss.zul.api.Tabs getTabsApi();

	/**
	 * Returns the tabpanels that this tabbox owns.
	 */
	public org.zkoss.zul.api.Tabpanels getTabpanelsApi();

	/**
	 * Returns whether the tab scrolling is enabled. Default: true.
	 * 
	 */
	public boolean isTabscroll();

	/**
	 * Sets whether to eable the tab scrolling
	 * 
	 */
	public void setTabscroll(boolean tabscroll);

	/**
	 * Returns the spacing between {@link Tabpanel}. This is used by certain
	 * molds, such as accordion.
	 * <p>
	 * Default: null (no spacing).
	 */
	public String getPanelSpacing();

	/**
	 * Sets the spacing between {@link Tabpanel}. This is used by certain molds,
	 * such as accordion.
	 */
	public void setPanelSpacing(String panelSpacing);

	/**
	 * Returns the selected index.
	 */
	public int getSelectedIndex();

	/***
	 * Sets the selected index.
	 */
	public void setSelectedIndex(int j);

	/**
	 * Returns the selected tab panel.
	 */
	public org.zkoss.zul.api.Tabpanel getSelectedPanelApi();

	/**
	 * Sets the selected tab panel.
	 */
	public void setSelectedPanelApi(Tabpanel panel);

	/**
	 * Returns the selected tab.
	 */
	public org.zkoss.zul.api.Tab getSelectedTabApi();

	/**
	 * Sets the selected tab.
	 */
	public void setSelectedTabApi(Tab tab);

	/**
	 * Returns the orient.
	 * 
	 * <p>
	 * Default: "horizontal".
	 * 
	 * <p>
	 * Note: only the default mold supports it (not supported if accordion).
	 */
	public String getOrient();

	/**
	 * Sets the orient.
	 * 
	 * @param orient
	 *            either "horizontal" or "vertical".
	 */
	public void setOrient(String orient) throws WrongValueException;

	/**
	 * Returns whether it is a horizontal tabbox.
	 * 
	 */
	public boolean isHorizontal();

	/**
	 * Returns whether it is a vertical tabbox.
	 * 
	 */
	public boolean isVertical();
}
