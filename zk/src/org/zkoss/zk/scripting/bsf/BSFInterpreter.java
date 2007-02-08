/* BSFInterpreter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Feb  6 11:38:54     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.bsf;

import org.apache.bsf.BSFException;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.scripting.util.NamespacelessInterpreter;

/**
 * Represents a BSF (Bean Scripting Framework) interpreter.
 *
 * @author tomyeh
 */
public class BSFInterpreter extends NamespacelessInterpreter {
	private final BSFManager _manager = new BSFManager();
	private final String _lang;

	/** Constructs a BSF interpreter.
	 *
	 * @param lang the language, say, Groovy.
	 * @param engineClassnm the class name of BSF engine
	 */
	public BSFInterpreter(String lang, String engineClassnm) {
		if (lang == null || lang.length() == 0
		|| engineClassnm == null || engineClassnm.length() == 0)
			throw new IllegalArgumentException("null or empty");
		try {
			_lang = lang;
			_manager.registerScriptingEngine(lang, engineClassnm, null);
			_manager.loadScriptingEngine(lang);
		} catch (BSFException ex) {
			throw new UiException(ex);
		}
	}

	//super//
	protected void exec(String script) {
		try {
			_manager.exec(_lang, "zk", 0, 0, script);
		} catch (BSFException ex) {
			throw new UiException(ex);
		}
	}
	protected void setVariable(String name, Object value) {
		try {
			if (value != null)
				_manager.declareBean(name, value, value.getClass());
			else
				_manager.declareBean(name, null, null);
		} catch (BSFException ex) {
			throw new UiException(ex);
		}
	}
	protected void unsetVariable(String name) {
		try {
			_manager.undeclareBean(name);
		} catch (BSFException ex) {
			throw new UiException(ex);
		}
	}

	//Interpreter//
	public Object getVariable(String name) {
		return _manager.getDeclaredBean(name);
	}
}
