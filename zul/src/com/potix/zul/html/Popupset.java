/* Popupset.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Sep 23 09:51:52     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.io.IOException;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.WrongValueException;

import com.potix.zul.html.impl.XulElement;

/**
 * A container for {@link Popup} elements.
 * You should declare all popup elements as children of a popupset.
 * This element does not directly display on screen.
 * Child popups will be displayed when asked to by other elements.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.4 $ $Date: 2006/05/29 04:28:25 $
 */
public class Popupset extends XulElement {
	public Popupset() {
		super.setVisible(false);
	}

	//-- Component --//
	/** Not allowd. */
	public boolean setVisible(boolean visible) {
		throw new UnsupportedOperationException("You cannot make it visible manually");
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		//we consider popup is an alias to menupopup
		if (!(child instanceof Menupopup))
			throw new UiException("Unsupported child for popupset: "+child);
		return super.insertBefore(child, insertBefore);
	}
}
