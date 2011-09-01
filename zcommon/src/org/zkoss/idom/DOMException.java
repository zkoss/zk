/* DOMException.java


	Purpose: 
	Description: 
	History:
	2001/09/28 14:04:16, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom;

import org.xml.sax.Locator;

import org.zkoss.lang.Objects;
import org.zkoss.idom.util.SimpleLocator;

/**
 * Denotes an operation is not supported.
 *
 * @author tomyeh
 */
public class DOMException extends org.w3c.dom.DOMException {
	protected Locator _loc;

	public DOMException(short code) {
		super(code, message(code, null, null));
	}
	public DOMException(short code, String extraMsg) {
		super(code, message(code, extraMsg, null));
	}
	public DOMException(short code, Locator loc) {
		super(code, message(code, null, loc));
		_loc = loc;
	}
	public DOMException(short code, String extraMsg, Locator loc) {
		super(code, message(code, extraMsg, loc));
		_loc = loc;
	}

	private static final String
	message(short code, String extraMsg, Locator loc) {
		StringBuffer sb = new StringBuffer(64);
		switch (code) {
		case HIERARCHY_REQUEST_ERR:
			sb.append("Hierarchy request error");
			break;
		case INVALID_ACCESS_ERR:
			sb.append("Invalid access to the underly object");
			break;
		case INVALID_CHARACTER_ERR:
			sb.append("Invalid character(s)");
			break;
		case NAMESPACE_ERR:
			sb.append("Namespace error");
			break;
		case NO_DATA_ALLOWED_ERR:
			sb.append("Data not allowed");
			break;
		case NO_MODIFICATION_ALLOWED_ERR:
			sb.append("No modification allowed");
			break;
		case NOT_FOUND_ERR:
			sb.append("Not found");
			break;
		case NOT_SUPPORTED_ERR:
			sb.append("Not supported yet");
			break;
		case SYNTAX_ERR:
			sb.append("Syntax error");
			break;
		default:
			sb.append("Unknown error");
			break;
		}
		if (extraMsg != null)
			sb.append(": ").append(extraMsg);

		if (loc != null) {
			if (sb.length() > 0)
				sb.append(' ');

			sb.append(SimpleLocator.toString(loc));
		}
		return sb.toString();
	}
}
