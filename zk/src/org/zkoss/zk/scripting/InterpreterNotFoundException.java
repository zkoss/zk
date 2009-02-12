/* InterpreterNotFoundException.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Feb  2 11:20:04     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting;

import org.zkoss.zk.ui.UiException;

/**
 * Denotes the interpreter of the requested scripting language not found.
 *
 * @author tomyeh
 */
public class InterpreterNotFoundException extends UiException {
	private final String _lang;

	/**
	 * @param lang the language name.
	 */
	public InterpreterNotFoundException(String lang, String msg, Throwable cause) {
		super(msg, cause);
		_lang = lang;
	}
	/**
	 * @param lang the language name.
	 */
	public InterpreterNotFoundException(String lang, String s) {
		super(s);
		_lang = lang;
	}
	/**
	 * @param lang the language name.
	 */
	public InterpreterNotFoundException(String lang, Throwable cause) {
		super(cause);
		_lang = lang;
	}
	/**
	 * @param lang the language name.
	 */
	public InterpreterNotFoundException(String lang) {
		_lang = lang;
	}

	/**
	 * @param lang the language name.
	 */
	public InterpreterNotFoundException(String lang, int code, Object[] fmtArgs, Throwable cause) {
		super(code, fmtArgs, cause);
		_lang = lang;
	}
	/**
	 * @param lang the language name.
	 */
	public InterpreterNotFoundException(String lang, int code, Object fmtArg, Throwable cause) {
		super(code, fmtArg, cause);
		_lang = lang;
	}
	/**
	 * @param lang the language name.
	 */
	public InterpreterNotFoundException(String lang, int code, Object[] fmtArgs) {
		super(code, fmtArgs);
		_lang = lang;
	}
	/**
	 * @param lang the language name.
	 */
	public InterpreterNotFoundException(String lang, int code, Object fmtArg) {
		super(code, fmtArg);
		_lang = lang;
	}
	/**
	 * @param lang the language name.
	 */
	public InterpreterNotFoundException(String lang, int code, Throwable cause) {
		super(code, cause);
		_lang = lang;
	}
	/**
	 * @param lang the language name.
	 */
	public InterpreterNotFoundException(String lang, int code) {
		super(code);
		_lang = lang;
	}

	/** Returns the lanaugage that is not found.
	 */
	public String getLanguage() {
		return _lang;
	}
}
