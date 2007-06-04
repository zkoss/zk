/* ZkTicker.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 29, 2007 3:46:26 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.impl;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Ticker;

import org.zkoss.zkmob.ZkComponent;


/**
 * @author henrichen
 *
 */
public class ZkTicker extends Ticker implements ZkComponent {
	private String _id;
	private Zk _zk;
	
	public ZkTicker(Zk zk, String id, String text) {
		super(text);
		_id = id;
		_zk = zk;
	}
	
	//--ZkComponent--//
	public String getId() {
		return _id;
	}
	
	public Zk getZk() {
		return _zk;
	}

	public void setAttr(String attr, String val) {
		//TODO:
	}
	
	public void addCommand(Command cmd) {
		//do nothing
	}
}
