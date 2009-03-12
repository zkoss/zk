/* WrongValuesException.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Oct 16, 2008 10:33:27 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

/** 
 * Denotes an exception that is an aggregation of multiple {@link WrongValueException}.
 *
 * @author jumperchen
 * @since 3.6.0
 */
public class WrongValuesException extends OperationException {
	private final WrongValueException[] _wves;
	/** Constructs a multiple wrong value exception.
	 * @param wrongValueExs an array of {@link WrongValueException}
	 * @exception IllegalArgumentException if wrongValueExs is null or empty
	 */
	public WrongValuesException(WrongValueException[] wrongValueExs) {
		if (wrongValueExs == null || wrongValueExs.length == 0)
			throw new IllegalArgumentException();
		_wves = wrongValueExs;
	}
	
	/** Returns all the exceptions (never null).
	 */
	public WrongValueException[] getWrongValueExceptions() {
		return _wves;
	}
}
