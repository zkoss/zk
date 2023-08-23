/* Slot.java

	Purpose:
		
	Description:
		
	History:
		12:08 PM 2023/8/23, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zhtml;

import org.zkoss.zhtml.impl.AbstractTag;
import org.zkoss.zk.ui.WrongValueException;

/**
 * The slot tag
 * @author jumperchen
 * @since 10.0.0
 */
public class Slot extends AbstractTag {
	public Slot() {
		super("slot");
	}

	/**
	 * Returns the name of this tag.
	 */
	public String getName() {
		return (String) getDynamicProperty("name");
	}

	/**
	 * Sets the name of this tag.
	 */
	public void setName(String name) throws WrongValueException {
		setDynamicProperty("name", name);
	}
}
