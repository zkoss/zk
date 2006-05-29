/* Bandpopup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Mar 20 12:31:44     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zul.html.impl.XulElement;

/**
 * The popup that belongs to a {@link Bandbox} instance.
 *
 * <p>Developer usually listen to the onOpen event that is sent to
 * {@link Bandbox} and then creates proper components as children
 * of this component.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/05/29 04:28:20 $
 */
public class Bandpopup extends XulElement {
	public Bandpopup() {
	}

	//-- super --//
	public boolean setVisible(boolean visible) {
		if (!visible)
			throw new UnsupportedOperationException("Use Bandbox.setOpen(false) instead");
		return true;
	}
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Bandbox))
			throw new UiException("Bandpopup's parent must be Bandbox");
		super.setParent(parent);
	}
}
