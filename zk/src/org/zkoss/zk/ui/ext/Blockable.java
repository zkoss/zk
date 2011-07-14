/* Blockable.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Jul 14, 2011 12:28:40 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext;

import org.zkoss.zk.au.AuRequest;

/**
 * An interface that allows a {@link org.zkoss.zk.ui.Component} to specify when 
 * to block an AuRequest from client, for security control.
 * @author simonpai
 * since 5.0.8
 */
public interface Blockable {
	
	/**
	 * Return true if the request shall be blocked.
	 */
	public boolean shallBlock(AuRequest request);
	
}
