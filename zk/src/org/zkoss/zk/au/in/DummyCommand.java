/* DummyCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jan 20 23:44:45     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.in;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;

/**
 * A dummy command that does nothing but triggers an execution
 * to prcess all pending request, if any.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class DummyCommand extends Command {
	public DummyCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}
	protected void process(AuRequest request) {
	}
}
