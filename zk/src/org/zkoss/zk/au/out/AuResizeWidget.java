/* AuResizeWidget.java

	Purpose:
		
	Description:
		
	History:
		Sat Aug 13 13:50:47 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.au.AuResponse;

/**
 * Used to force ZK Client re-calculate the size of the widget at the client.
 * @author tomyeh
 * @since 5.0.8
 */
public class AuResizeWidget extends AuResponse {
	/** Constructs a command to to force ZK Client re-calculate the size of
	 * the given widget.
	 * @param comp the component to resize
	 */
	public AuResizeWidget(Component comp) {
		super("resizeWgt", comp, comp);
	}

	/** Default: zk.resizeWgt (i.e., only one response of this class will
	 * be sent to the client in an execution for the same component)
	 */
	public final String getOverrideKey() {
		return "zk.resizeWgt";
	}
}
