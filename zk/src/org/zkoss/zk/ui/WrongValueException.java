/* WrongValueException.java

	Purpose:
		
	Description:
		
	History:
		Mon Aug  8 18:30:24     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

/**
 * Denotes the value passed to a setter (aka., mutator) of a component
 * is wrong.
 *
 * @author tomyeh
 */
public class WrongValueException extends OperationException {
	private Component _comp;

	/** Contructs a wrong value exception to show an error box at the client.
	 * @param comp the component that causes this exception.
	 */
	public WrongValueException(Component comp, String msg) {
		super(msg);
		_comp = comp;
	}
	/** Contructs a wrong value exception to show an error box at the client.
	 * @param comp the component that causes this exception.
	 */
	public WrongValueException(Component comp, int code, Object[] fmtArgs) {
		super(code, fmtArgs);
		_comp = comp;
	}
	/** Contructs a wrong value exception to show an error box at the client.
	 * @param comp the component that causes this exception.
	 */
	public WrongValueException(Component comp, int code, Object fmtArg) {
		super(code, fmtArg);
		_comp = comp;
	}
	/** Contructs a wrong value exception to show an error box at the client.
	 * @param comp the component that causes this exception.
	 */
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
