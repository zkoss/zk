/* WrongValueException.java

{{IS_NOTE
	$Id: WrongValueException.java,v 1.2 2006/02/27 03:54:48 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Mon Aug  8 18:30:24     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui;

/**
 * Denotes the value passed to a setter (aka., mutator) of a component
 * is wrong.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:54:48 $
 */
public class WrongValueException extends OperationException {
	private Component _comp;

	/** @param comp the component that causes this exception. */
	public WrongValueException(Component comp, String msg) {
		super(msg);
		_comp = comp;
	}
	/** @param comp the component that causes this exception. */
	public WrongValueException(Component comp, int code, Object[] fmtArgs) {
		super(code, fmtArgs);
		_comp = comp;
	}
	/** @param comp the component that causes this exception. */
	public WrongValueException(Component comp, int code, Object fmtArg) {
		super(code, fmtArg);
		_comp = comp;
	}
	/** @param comp the component that causes this exception. */
	public WrongValueException(Component comp, int code) {
		super(code);
		_comp = comp;
	}

	public WrongValueException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public WrongValueException(String s) {
		super(s);
	}
	public WrongValueException(Throwable cause) {
		super(cause);
	}
	public WrongValueException() {
	}

	public WrongValueException(int code, Object[] fmtArgs, Throwable cause) {
		super(code, fmtArgs, cause);
	}
	public WrongValueException(int code, Object fmtArg, Throwable cause) {
		super(code, fmtArg, cause);
	}
	public WrongValueException(int code, Object[] fmtArgs) {
		super(code, fmtArgs);
	}
	public WrongValueException(int code, Object fmtArg) {
		super(code, fmtArg);
	}
	public WrongValueException(int code, Throwable cause) {
		super(code, cause);
	}
	public WrongValueException(int code) {
		super(code);
	}

	/** Returns the component that causes this exception, or null.
	 */
	public Component getComponent() {
		return _comp;
	}
}
