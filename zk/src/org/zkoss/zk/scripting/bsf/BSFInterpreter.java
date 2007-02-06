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
import org.zkoss.zk.scripting.Interpreter;
import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.VariableResolver;

/**
 * Represents a BSF (Bean Scripting Framework) interpreter.
 *
 * @author tomyeh
 */
public class BSFInterpreter implements Interpreter {
	private final BSFManager _manager = new BSFManager();
	private final String _lang;
	private final Namespace _ns = new GlobalNamespace(_manager);

	/** Constructs a BSF interpreter.
	 *
	 * @param lang the language, say, Groovy.
	 * @param engineClassnm the class name of BSF engine
	 */
	public BSFInterpreter(String lang, String engineClassnm) {
		if (lang == null || lang.length() == 0
		|| engineClassnm == null || engineClassnm.length() == 0)
			throw new IllegalArgumentException("null or empty");
		_manager.registerScriptingEngine(lang, engineClassnm, null);
		_lang = lang;
	}

	//Interpreter//
	public Namespace getNamespace() {
		return _ns;
	}
	public void setVariable(String name, Object val) {
		_ns.setVariable(name, val, true);
	}
	public Object getVariable(String name) {
		return _ns.getVariable(name, true);
	}
	public void unsetVariable(String name) {
		_ns.unsetVariable(name);
	}
	public boolean addVariableResolver(VariableResolver resolver) {
		throw new UnsupportedOperationException("BSF doesn't support variable resolver");
	}
	public boolean removeVariableResolver(VariableResolver resolver) {
		return false;
	}
	public void interpret(String script, Namespace ns) {
		try {
			_manager.exec(_lang, "zk", 0, 0, script);
		} catch (BSFException ex) {
			throw new UiException(ex);
		}
	}
}
