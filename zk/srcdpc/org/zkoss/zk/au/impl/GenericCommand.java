/* GenericCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 18 18:09:58     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.impl;

/**
 * Deprecated: used to implement a generic command.
 *
 * @author tomyeh
 * @deprecated As of release 3.0.0, replaced by {@link org.zkoss.zk.au.in.GenericCommand}
 */
public class GenericCommand extends org.zkoss.zk.au.in.GenericCommand {
	public GenericCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}
	/**
	 * @param broadcast whether to broadcast the event to all root components
	 * of all pages in the same desktop, if component is null.
	 */
	public GenericCommand(String evtnm, int flags, boolean broadcast) {
		super(evtnm, flags, broadcast);
	}
}
