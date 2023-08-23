/* Dialog.java

	Purpose:
		
	Description:
		
	History:
		4:47 PM 2023/8/22, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zhtml;

import org.zkoss.zhtml.impl.AbstractTag;
import org.zkoss.zk.ui.WrongValueException;

/**
 * The dialog tag
 * @author jumperchen
 * @since 10.0.0
 */
public class Dialog extends AbstractTag {
	public Dialog() {
		super("dialog");
	}

	/**
	 * Returns the open of this dialog tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 */
	public boolean isOpen() {
		Boolean open = (Boolean) getDynamicProperty("open");
		return open != null && open;
	}

	/**
	 * Sets the open of this dialog tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 */
	public void setOpen(boolean open) throws WrongValueException {
		setDynamicProperty("open", open);
	}
}
