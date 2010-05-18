/* AuUuid.java

	Purpose:
		
	Description:
		
	History:
		Tue May 18 11:51:31 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.au.AuResponse;

/**
 * Rename the UUID.
 * @author tomyeh
 * @since 5.0.3
 */
public class AuUuid extends AuResponse {
	/**
	 * @param comp the component whose UUID has been changed.
	 * @param oldid the old UUID.
	 */
	public AuUuid(Component comp, String oldid) {
		super("uuid", comp, new String[] {oldid, comp.getUuid()});
	}
}
