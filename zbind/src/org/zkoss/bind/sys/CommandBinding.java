/* CommandBinding.java

	Purpose:
		
	Description:
		
	History:
		Jul 26, 2011 3:55:28 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys;

import org.zkoss.bind.BindContext;


/**
 * A binding tells which command to call(usually used with UI event).
 * @author henrichen
 *
 */
public interface CommandBinding extends Binding {
	/**
	 * Call the command associated with this binding.
	 * @param ctx the binding runtime context 
	 */
	public void execute(BindContext ctx);
	
	/**
	 * Returns the command expression in string.
	 * @return the command expression in string.
	 */
	public String getCommandString();
}
