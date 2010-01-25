/* AuClearBusy.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 21 15:35:51 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.au.AuResponse;

/**
 * A response to ask the client to clear the busy message.
 * 
 * @author tomyeh
 * @see AuShowBusy
 * @since 5.0.0
 */
public class AuClearBusy extends AuResponse {
	/** Constructs a command to remove a busy message covering the whole browser.
	 */
	public AuClearBusy() {
		super("clearBusy");
	}
	/** Constructs a command to remove a busy message covering only the specified component.
	 * @param comp the component that the busy message is associated.
	 */
	public AuClearBusy(Component comp) {
		super("clearBusy", comp, comp.getUuid());
	}
}
