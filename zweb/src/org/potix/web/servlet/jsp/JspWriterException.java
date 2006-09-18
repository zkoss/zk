/* JspWriterException.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri May 28 10:06:46     2004, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.servlet.jsp;

/**
 * Denotes unable to write to an connection.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class JspWriterException extends JspTagException {
	public JspWriterException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public JspWriterException(String s) {
		super(s);
	}
	public JspWriterException(Throwable cause) {
		super(cause);
	}
	public JspWriterException() {
	}

	public JspWriterException(int code, Object[] fmtArgs, Throwable cause) {
		super(code, fmtArgs, cause);
	}
	public JspWriterException(int code, Object fmtArg, Throwable cause) {
		super(code, fmtArg, cause);
	}
	public JspWriterException(int code, Object[] fmtArgs) {
		super(code, fmtArgs);
	}
	public JspWriterException(int code, Object fmtArg) {
		super(code, fmtArg);
	}
	public JspWriterException(int code, Throwable cause) {
		super(code, cause);
	}
	public JspWriterException(int code) {
		super(code);
	}
}
