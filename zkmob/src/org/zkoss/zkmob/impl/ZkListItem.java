/* ImageableListItem.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 22, 2007 8:54:13 AM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.impl;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;

import org.zkoss.zkmob.Imageable;
import org.zkoss.zkmob.Listable;
import org.zkoss.zkmob.ZkComponent;

/**
 * A data carrier used for constructing list item of a Listable.
 *  
 * @author henrichen
 */
public class ZkListItem implements ZkComponent, Imageable {
	private Listable _parent; 
	private String _id;
	
	public ZkListItem(String id, Listable parent) {
		_parent = parent;
		_id = id;
	}
	
	public Listable getOwner() {
		return _parent;
	}
	
	//--Imageable--//
	public void loadImage(Image image) {
		final int index = _parent.indexOf(_id);
		_parent.set(index, _parent.getString(index), image);
	}
	
	//--ZkComponent--//
	public String getId() {
		return _id;
	}
	
	public Zk getZk() {
		return ((ZkComponent)_parent).getZk();
	}

	public void setAttr(String attr, String val) {
		//do nothing
	}
	
	public void addCommand(Command cmd) {
		//do nothing
	}
}
