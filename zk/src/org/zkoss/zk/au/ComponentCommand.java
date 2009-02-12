/* ComponentCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Apr 25 14:29:27     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au;

/**
 * A sketetal implementation for component-specific commands.
 * A component-specific command is visible by calling
 * {@link org.zkoss.zk.ui.sys.ComponentCtrl#getCommand}.
 *
 * @author tomyeh
 * @since 3.0.5
 */
abstract public class ComponentCommand extends Command {
	/** Constructs a component-specific command.
	 * @param flags a combination of {@link #SKIP_IF_EVER_ERROR},
	 * {@link #IGNORABLE} and others
	 */
	public ComponentCommand(String id, int flags) {
		super(id, flags | COMPONENT_SPECIFIC);
	}
}
