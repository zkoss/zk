/* GlobalCommandEvent.java

	Purpose:
		
	Description:
		
	History:
		2012/1/31 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * queue event for global command, internal use only
 * @author dennis
 * @since 6.0.0
 */
public class GlobalCommandEvent  extends Event {
	private static final long serialVersionUID = 201109091736L;
	String _command; 
	Map<String, Object> _args;

	public GlobalCommandEvent(Component comp, String command, Map<String, Object> args) {
		super("onGlobalCommand", comp);
		this._command = command;
		this._args = args;
	}

	public String getCommand() {
		return _command;
	}
	
	public Map<String, Object> getArgs() {
		return _args;
	}
	
}
