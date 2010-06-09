/* WarningException.java

	Purpose:
		
	Description:
		
	History:
		Mon Oct 27 10:19:59     2003, Created by tomyeh

Copyright (C) 2003 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.lang;

import org.zkoss.mesg.Messageable;

/**
 * The warning exception.
 *
 * @author tomyeh
 */
public class WarningException extends SystemException implements Warning {
	public WarningException(String msg, Throwable cause) {
		super(msg, cause);
		updateCode(cause);
	}
	public WarningException(Throwable cause) {
		super(cause);
		updateCode(cause);
	}

	public WarningException(int code, Object[] fmtArgs, Throwable cause) {
		super(code, fmtArgs, cause);
	}
	public WarningException(int code, Object fmtArg, Throwable cause) {
		super(code, fmtArg, cause);
	}
	public WarningException(int code, Object[] fmtArgs) {
		super(code, fmtArgs);
	}
	public WarningException(int code, Object fmtArg) {
		super(code, fmtArg);
	}
	public WarningException(int code, Throwable cause) {
		super(code, cause);
	}
	public WarningException(int code) {
		super(code);
	}

	private void updateCode(Throwable cause) {
		if (!(cause instanceof Messageable))
			throw new IllegalArgumentException("cause of WarningException must be Messageable: "+(cause != null?cause.getClass():null));
		_code = ((Messageable)cause).getCode();
	}
}
