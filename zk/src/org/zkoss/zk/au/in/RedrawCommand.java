/* RedrawCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 11 16:34:56     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.au.in;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;

/**
 * Used only by {@link AuRequest} to implement the redraw command.
 *
 * <p>If uuid is specified, the component is invalidated.
 * If not specified, the whole desktop is invalidated.
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class RedrawCommand extends Command {
	public RedrawCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	//-- super --//
	protected void process(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp != null) comp.invalidate();
		else request.getDesktop().invalidate();
	}
}
