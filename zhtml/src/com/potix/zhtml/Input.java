/* Input.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Nov 29 21:59:11     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zhtml;

import com.potix.zk.ui.WrongValueException;
import com.potix.zk.ui.ext.Inputable;
import com.potix.zhtml.impl.AbstractTag;

/**
 * The input tag.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Input extends AbstractTag implements Inputable {
	public Input() {
		super("input");
	}
	protected Input(String tagnm) {
		super(tagnm);
	}

	/** Returns the value of this input.
	 */
	public String getValue() {
		return (String)getDynamicProperty("value");
	}
	/** Sets the vallue of this input.
	 */
	public void setValue(String value) throws WrongValueException {
		setDynamicProperty("value", value);
	}

	//-- Inputable --//
	public void setTextByClient(String value) throws WrongValueException {
		setValue(value);
	}
}
