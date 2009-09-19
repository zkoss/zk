/* DefinitionNotFoundException.java

	Purpose:
		
	Description:
		
	History:
		Sun Jun  5 12:05:40     2005, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import org.zkoss.zk.ui.UiException;

/**
 * Dentoes a definition cannot be found.
 *
 * @author tomyeh
 */
public class DefinitionNotFoundException extends UiException {
	public DefinitionNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public DefinitionNotFoundException(String s) {
		super(s);
	}
	public DefinitionNotFoundException(Throwable cause) {
		super(cause);
	}
	public DefinitionNotFoundException() {
	}

	public DefinitionNotFoundException(int code, Object[] fmtArgs, Throwable cause) {
		super(code, fmtArgs, cause);
	}
	public DefinitionNotFoundException(int code, Object fmtArg, Throwable cause) {
		super(code, fmtArg, cause);
	}
	public DefinitionNotFoundException(int code, Object[] fmtArgs) {
		super(code, fmtArgs);
	}
	public DefinitionNotFoundException(int code, Object fmtArg) {
		super(code, fmtArg);
	}
	public DefinitionNotFoundException(int code, Throwable cause) {
		super(code, cause);
	}
	public DefinitionNotFoundException(int code) {
		super(code);
	}
}
