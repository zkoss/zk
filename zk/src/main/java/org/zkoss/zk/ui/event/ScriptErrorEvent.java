/* ScriptErrorEvent.java

	Purpose:
		
	Description:
		
	History:
		Fri Jul 2 12:00:24 CST 2021, Created by jameschu

Copyright (C) 2017 2021 Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.event;

import java.util.Map;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;

/**
 * A script error event used with <code>onScriptError</code>
 * When using Clients.evalJavascript and any javascript error event happened, user can handle the error in the server side.
 *
 * @author jameschu
 * @since 9.6.0
 */
public class ScriptErrorEvent extends Event {
	private final String _message;
	private final String _stack;
	private static Component _dummyTarget;

	public ScriptErrorEvent(String name, Component target, String message, String stack) {
		super(name, target);
		this._message = message;
		this._stack = stack;
	}

	private static Component initDummyTarget() {
		if (_dummyTarget == null)
			_dummyTarget = new AbstractComponent();
		return _dummyTarget;
	}

	/**
	 * Converts an AU request to a script error event.
	 */
	public static final ScriptErrorEvent getScriptErrorEvent(AuRequest request) {
		Map<String, Object> data = request.getData();
		return new ScriptErrorEvent(request.getCommand(), initDummyTarget(), (String) data.get("message"), (String) data.get("stack"));
	}

	public String getStack() {
		return _stack;
	}

	public String getMessage() {
		return _message;
	}
}
