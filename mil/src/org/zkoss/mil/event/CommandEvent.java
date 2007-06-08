/* CommandEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 1, 2007 3:26:55 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.mil.event;

import org.zkoss.mil.Command;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * Used to notify which MIL Command has been triggered.
 * @author henrichen
 * @since 2.4.0
 */
public class CommandEvent extends Event {
	private Command _command;
	public CommandEvent(String evtnm, Component target, Command cmd) {
		super(evtnm, target);
		_command = cmd;
	}
	
	/**
	 * Return the MIL Command that trigers this event.
	 * @return the MIL Command that trigers this event.
	 */
	public Command getCommand() {
		return _command;
	}
}
