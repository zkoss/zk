/* AuNotification.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Mar 15, 2012 4:37:25 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;

/**
 * The au object for notification.
 * @author simonpai
 * @since 6.0.1
 */
public class AuNotification extends AuResponse {
	
	/** Shows notification at predefined position.
	 */
	public AuNotification(String msg, String type, Page page, Component ref, 
			String position, int duration, boolean closable) {
		super("showNotification", new Object[] { 
				msg, type, page.getUuid(), ref, position, null, duration, closable });
	}
	
	/** Shows notification at given coordinate
	 */
	public AuNotification(String msg, String type, Page page, Component ref, 
			int x, int y, int duration, boolean closable) {
		super("showNotification", new Object[] { 
				msg, type, page.getUuid(), ref, null, new int[]{x, y}, duration, closable });
	}
	
}
