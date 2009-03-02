/* WrongValuesException.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Oct 16, 2008 10:33:27 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

/** 
 * Denotes the value passed to a setter (aka., mutator) of a component
 * is wrong for a group components.
 * @author jumperchen
 * @since 3.6.0
 */
public class WrongValuesException extends OperationException {
	private WrongValueException[] _wrongValues;
	public WrongValuesException(WrongValueException[] wrongValues) {
		_wrongValues = wrongValues;
	}
	
	/** Returns all the exceptions, or null.
	 */
	public WrongValueException[] getWrongValueExceptions() {
		return _wrongValues;
	}
}
