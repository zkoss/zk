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
