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
package org.zkoss.zkmob.ui;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.List;

import org.zkoss.zkmob.UiManager;
import org.zkoss.zkmob.ZkComponent;

/**
 * A ZK Command.
 * @author henrichen
 *
 */
public class ZkCommand extends Command implements ZkComponent {
	private String _id;
	private ZkComponent _parent;
	private int _type;
	
	public ZkCommand(String id, String label, String longLabel, int type, int priority) {
		super(label, longLabel, type == 0x100 ? Command.OK : type, priority);
		_id = id;
		_type = type;
	}
	
	public void setParent(ZkComponent parent) {
		if (parent != _parent) {
			if (_parent != null) {
				_parent.removeCommand(this);
			}
			if (parent != null) {
				if (_type == 0x100) { //special selection command
					if (parent instanceof List) {
						((List) parent).setSelectCommand(this);
					}
				} else {
					parent.addCommand(this);
				}
				UiManager.registerCommand(parent, this, parent.getZkDesktop());
			}
			_parent = parent;
		}
	}
	
	public ZkComponent getParent() {
		return _parent;
	}

	public void addCommand(Command cmd) {
		// do nothing
	}
	
	public void removeCommand(Command cmd) {
		//do nothing
	}

	public String getId() {
		return _id;
	}

	public ZkDesktop getZkDesktop() {
		return _parent == null ? null : _parent.getZkDesktop();
	}

	public void setAttr(String attr, String val) {
		// TODO Auto-generated method stub

	}
}