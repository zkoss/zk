/* Inputable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 31, 2007 5:23:48 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob;

/**
 * @author henrichen
 *
 */
public interface Inputable {
	/**
	 * Check whether send onChange event.
	 * @return null means don't send; true means send asap; false means wait until kick to send 
	 */
	public Boolean getOnChange();

	/**
	 * Check whether send onChanging event.
	 * @return null means don't send; true means send asap; false means wait until kick to send 
	 */
	public Boolean getOnChanging();

	/**
	 * Get input text.
	 * @return input text.
	 */
	public String getString();
}
