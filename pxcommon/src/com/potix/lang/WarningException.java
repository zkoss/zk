/* WarningException.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Oct 27 10:19:59     2003, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2003 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.lang;

import com.potix.mesg.Messageable;

/**
 * The warning exception.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/05/29 04:27:21 $
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
