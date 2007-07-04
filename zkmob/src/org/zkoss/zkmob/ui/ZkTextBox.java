/* ZkTextBox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 29, 2007 3:33:11 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.ui;

import javax.microedition.lcdui.TextBox;

import org.zkoss.zkmob.ZkComponent;


/**
 * ZK TextBox.
 * @author henrichen
 *
 */
public class ZkTextBox extends TextBox implements ZkComponent {
	private String _id;
	private ZkDesktop _zk;
	
	public ZkTextBox(ZkDesktop zk, String id, String title, String text, int maxSize, int constraints) {
		super(title, text, maxSize, constraints);
		_id = id;
		_zk = zk;
	}
	
	//--ZkComponent--//
	public String getId() {
		return _id;
	}
	
	public void setParent(ZkComponent parent) {
		_zk = (ZkDesktop) parent;
	}
	
	public ZkComponent getParent() {
		return getZkDesktop();
	}

	public ZkDesktop getZkDesktop() {
		return _zk;
	}

	public void setAttr(String attr, String val) {
		//TODO:
	}
}
