/* GlobalCommandEvent.java

	Purpose:
		
	Description:
		
	History:
		2012/1/31 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * The global-command event
 * @author dennis
 * @since 6.0.1
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

	/**
	 * Gets the global command name
	 */
	public String getCommand() {
		return _command;
	}
	
	/**
	 * Gets the global-command args.
	 */
	public Map<String, Object> getArgs() {
		return _args;
	}
	
}
