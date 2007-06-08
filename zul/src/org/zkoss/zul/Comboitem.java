/* Comboitem.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Dec 15 17:33:35     2005, Created by tomyeh
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
import org.zkoss.zul.impl.LabelImageElement;

/**
 * An item of a combo box.
 *
 * <p>Non-XUL extension. Refer to {@link Combobox}.
 * 
 * <p>Default {@link #getSclass}: item.
 *
 * @author tomyeh
 * @see Combobox
 */
public class Comboitem extends LabelImageElement {
	private String _desc = "";
	private Object _value;

	public Comboitem() {
		setSclass("item");
	}
	public Comboitem(String label) {
		this();
		setLabel(label);
	}
	public Comboitem(String label, String image) {
		this();
		setLabel(label);
		setImage(image);
	}

	/** Returns the description (never null).
	 * The description is used to provide extra information such that
	 * users is easy to make a selection.
	 * <p>Default: "".
	 */
	public String getDescription() {
		return _desc;
	}
	/** Sets the description.
	 */
	public void setDescription(String desc) {
		if (desc == null) desc = "";
		if (!_desc.equals(desc)) {
			_desc = desc;
			invalidate();
		}
	}

	/** Returns the value associated with this combo item.
	 * The value is application dependent. It can be anything.
	 *
	 * <p>It is usually used with {@link Combobox#getSelectedItem}.
	 * For example,
	 * <code>combobox.getSelectedItem().getValue()</code>
	 *
	 * @see Combobox#getSelectedItem
	 * @see #setValue
	 * @since 2.4.0
	 */
	public Object getValue() {
		return _value;
	}
	/** Associate the value with this combo item.
	 * The value is application dependent. It can be anything.
	 *
	 * @see Combobox#getSelectedItem
	 * @see #getValue
	 * @since 2.4.0
	 */
	public void setValue(Object value) {
		_value = value;
	}

	//-- super --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Combobox))
			throw new UiException("Comboitem's parent must be Combobox");
		super.setParent(parent);
	}

	/** No child is allowed. */
	public boolean isChildable() {
		return false;
	}
}
