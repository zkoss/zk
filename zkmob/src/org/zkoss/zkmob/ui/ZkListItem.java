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
package org.zkoss.zkmob.ui;

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
	private String _label;
	private String _image;
	
	public ZkListItem(Listable parent, String id, String label, String image) {
		_parent = parent;
		_id = id;
		_label = label;
		_image = image;
	}
	public void setParent(ZkComponent parent) {
		_parent = (Listable) parent;
	}
	
	public ZkComponent getParent() {
		return (ZkComponent) _parent;
	}
	
	public String getLabel() {
		return _label;
	}
	
	public String getImage() {
		return _image;
	}
	
	//--Imageable--//
	public void loadImage(Image image) {
		final int index = _parent.indexOf(this);
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
