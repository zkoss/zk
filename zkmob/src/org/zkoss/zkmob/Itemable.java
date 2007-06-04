/* Itemable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 4, 2007 2:26:03 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob;

import javax.microedition.lcdui.Form;

/**
 * An item kind of Component who knows the owner Form.
 * 
 * @author henrichen
 */
public interface Itemable {
	/**
	 * Get the owner Form of this Item.
	 * @return the owner Form.
	 */
	public Form getForm();
	
	/**
	 * Set the owner Form.
	 */
	public void setForm(Form form);
}
