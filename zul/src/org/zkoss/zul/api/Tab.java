/* Tab.java

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

/**
 * A tab.
 * <p>
 * Default {@link #getZclass}: z-tab. (since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 * 
 */
public interface Tab extends org.zkoss.zul.impl.api.LabelImageElement {

	/**
	 * Returns whether this tab is closable. If closable, a button is displayed
	 * and the onClose event is sent if an user clicks the button.
	 * <p>
	 * Default: false.
	 */
	public boolean isClosable();

	/**
	 * Sets whether this tab is closable. If closable, a button is displayed and
	 * the onClose event is sent if an user clicks the button.
	 * <p>
	 * Default: false.
	 * <p>
	 * You can intercept the default behavior by either overriding
	 * {@link org.zkoss.zul.Tab#onClose}, or listening the onClose event.
	 */
	public void setClosable(boolean closable);

	/**
	 * Returns the tabbox owns this component.
	 */
	public org.zkoss.zul.api.Tabbox getTabboxApi();

	/**
	 * Returns the panel associated with this tab.
	 */
	public org.zkoss.zul.api.Tabpanel getLinkedPanelApi();

	/**
	 * Sets whether this tab is selected.
	 */
	public void setSelected(boolean selected);

	/**
	 * Sets whether this tab is disabled. If a tab is disabled, then it cann't
	 * be selected or closed by user, but it still can be controlled by server
	 * side program.
	 * 
	 */
	public void setDisabled(boolean disabled);

	/**
	 * Returns the index of this panel, or -1 if it doesn't belong to any tabs.
	 */
	public int getIndex();

}
