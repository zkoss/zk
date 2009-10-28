/* Comboitem.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 09:27:29     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

/**
 * An item of a combo box.
 * 
 * <p>
 * Non-XUL extension. Refer to {@link Combobox}.
 * 
 * <p>
 * Default {@link #getZclass}: z-comboitem. (since 5.0.0)
 * 
 * @author tomyeh
 * @see Combobox
 * @since 3.5.2
 */
public interface Comboitem extends org.zkoss.zul.impl.api.LabelImageElement,
org.zkoss.zk.ui.ext.Disable {

	/**
	 * Sets whether it is disabled.
	 */
	public void setDisabled(boolean disabled);

	/**
	 * Returns whether it is disabled.
	 * <p>
	 * Default: false.
	 * 
	 */
	public boolean isDisabled();

	/**
	 * Returns the description (never null). The description is used to provide
	 * extra information such that users is easy to make a selection.
	 * <p>
	 * Default: "".
	 */
	public String getDescription();

	/**
	 * Sets the description.
	 */
	public void setDescription(String desc);

	/**
	 * Returns the embedded content (i.e., HTML tags) that is shown as part of
	 * the description.
	 * 
	 * <p>
	 * It is useful to show the description in more versatile way.
	 * 
	 * <p>
	 * Default: empty ("").
	 * 
	 * @see #getDescription
	 */
	public String getContent();

	/**
	 * Sets the embedded content (i.e., HTML tags) that is shown as part of the
	 * description.
	 * 
	 * <p>
	 * It is useful to show the description in more versatile way.
	 * 
	 * @see #setDescription
	 */
	public void setContent(String content);

	/**
	 * Returns the value associated with this combo item. The value is
	 * application dependent. It can be anything.
	 * 
	 * <p>
	 * It is usually used with {@link Combobox#getSelectedItemApi}. For example,
	 * <code>combobox.getSelectedItem().getValue()</code>
	 * 
	 * @see org.zkoss.zul.Combobox#getSelectedItem
	 * @see #setValue
	 */
	public Object getValue();

	/**
	 * Associate the value with this combo item. The value is application
	 * dependent. It can be anything.
	 * 
	 * @see org.zkoss.zul.Combobox#getSelectedItem
	 * @see #getValue
	 */
	public void setValue(Object value);

}
