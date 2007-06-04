/* ZkCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 1, 2007 12:17:20 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.impl;

import javax.microedition.lcdui.Command;

import org.zkoss.zkmob.ZkComponent;

/**
 * A ZK Command.
 * @author henrichen
 *
 */
public class ZkCommand extends Command implements ZkComponent {
	private String _id;
	private Zk _zk;
	private ZkComponent _owner;
	
	public ZkCommand(ZkComponent owner, Zk zk, String id, String label, String longLabel, int type, int priority) {
		super(label, longLabel, type, priority);
		_id = id;
		_zk = zk;
		_owner = owner;
		
		owner.addCommand(this);
	}

	public void addCommand(Command cmd) {
		// do nothing
	}

	public String getId() {
		return _id;
	}

	public Zk getZk() {
		return _zk;
	}

	public void setAttr(String attr, String val) {
		// TODO Auto-generated method stub

	}

}
