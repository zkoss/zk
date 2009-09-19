/* PropertyNotFoundException.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 16 15:38:52     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import org.zkoss.zk.ui.UiException;

/**
 * Represents the a property (aka., a method of a component) is not found.
 * @author tomyeh
 * @since 3.0.5
 */
public class PropertyNotFoundException extends UiException {
	public PropertyNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public PropertyNotFoundException(String s) {
		super(s);
	}
	public PropertyNotFoundException(Throwable cause) {
		super(cause);
	}
	public PropertyNotFoundException() {
	}

	public PropertyNotFoundException(int code, Object[] fmtArgs, Throwable cause) {
		super(code, fmtArgs, cause);
	}
	public PropertyNotFoundException(int code, Object fmtArg, Throwable cause) {
		super(code, fmtArg, cause);
	}
	public PropertyNotFoundException(int code, Object[] fmtArgs) {
		super(code, fmtArgs);
	}
	public PropertyNotFoundException(int code, Object fmtArg) {
		super(code, fmtArg);
	}
	public PropertyNotFoundException(int code, Throwable cause) {
		super(code, cause);
	}
	public PropertyNotFoundException(int code) {
		super(code);
	}
}
