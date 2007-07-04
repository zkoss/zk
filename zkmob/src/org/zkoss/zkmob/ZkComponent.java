/* Identifiable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 29, 2007 4:01:52 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob;

import javax.microedition.lcdui.Command;

import org.zkoss.zkmob.ui.ZkDesktop;

/**
 * A ZK component.
 * 
 * @author henrichen
 */
public interface ZkComponent {
	/**
	 * Get Id.
	 * @return id
	 */
	public String getId();
	
	/**
	 * Get ZK Desktop Controller.
	 */
	public ZkDesktop getZkDesktop();
	
	/**
	 * Get Parent.
	 */
	public ZkComponent getParent();
	
	/**
	 * Set Parent.
	 */
	public void setParent(ZkComponent parent);

	/**
	 * Add Command to this component.
	 */
	public void addCommand(Command cmd);
	
	/**
	 * Update a specific attribute of this Zk component.
	 * @param attr
	 * @param val
	 */
	public void setAttr(String attr, String val);
}
