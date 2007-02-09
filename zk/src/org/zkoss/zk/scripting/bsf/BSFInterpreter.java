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

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.scripting.util.NamespacelessInterpreter;

/**
 * Represents a BSF (Bean Scripting Framework) interpreter.
 *
 * @author tomyeh
 */
public class BSFInterpreter extends NamespacelessInterpreter {
	private final String _engcls;
	private BSFManager _manager = new BSFManager();

	/** Constructs a BSF interpreter.
	 *
	 * @param engineClassnm the class name of BSF engine
	 */
	protected BSFInterpreter(String engineClassnm) {
		if (engineClassnm == null || engineClassnm.length() == 0)
			throw new IllegalArgumentException("null or empty");
		_engcls = engineClassnm;
	}

	//Interpreter//
	public void init(Page owner, String zslang) {
		try {
			_manager.registerScriptingEngine(zslang, _engcls, null);
			_manager.loadScriptingEngine(zslang);
		} catch (BSFException ex) {
			throw new UiException(ex);
		}
	}

	//NamespacelessInterpreter//
	protected void exec(String script) {
		try {
			_manager.exec(getZScriptLanguage(), "zk", 0, 0, script);
		} catch (BSFException ex) {
			throw new UiException(ex);
		}
	}
	protected void set(String name, Object value) {
		try {
			if (value != null)
				_manager.declareBean(name, value, value.getClass());
			else
				_manager.declareBean(name, null, null);
		} catch (BSFException ex) {
			throw new UiException(ex);
		}
	}
	protected void unset(String name) {
		try {
			_manager.undeclareBean(name);
		} catch (BSFException ex) {
			throw new UiException(ex);
		}
	}
	protected Object get(String name) {
		return _manager.getDeclaredBean(name);
	}
}
