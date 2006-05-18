/* Comboitem.java

{{IS_NOTE
	$Id: Comboitem.java,v 1.8 2006/04/17 06:39:56 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Thu Dec 15 17:33:35     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zul.html.impl.LabelImageElement;

/**
 * An item of a combo box.
 *
 * <p>Non-XUL extension. Refer to {@link Combobox}.
 * 
 * <p>Default {@link #getSclass}: item.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.8 $ $Date: 2006/04/17 06:39:56 $
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
			invalidate(INNER);
		}
	}

	//-- super --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Combobox))
			throw new UiException("Comboitem's parent must be Combobox");
		super.setParent(parent);
	}

	/** Childable. */
	public boolean isChildable() {
		return false;
	}
}
