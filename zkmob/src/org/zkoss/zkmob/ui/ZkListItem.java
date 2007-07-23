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
import org.zkoss.zkmob.UiManager;
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
	
	public ZkListItem(String id, String label, String image) {
		_id = id;
		_label = label;
		_image = image;
	}

	public void setParent(ZkComponent parent) {
		if (_parent != parent) {
			_parent = (Listable) parent;
			((Listable)_parent).appendChild(this);
		}
	}
	
	public ZkComponent getParent() {
		return (ZkComponent) _parent;
	}
	
	public String getLabel() {
		return _label;
	}
	
	//--Imageable--//
	public void loadImage(Image image) {
		final int index = _parent.indexOf(this);
		_parent.set(index, _parent.getString(index), image);
	}
	
	public String getImageSrc() {
		return _image;
	}
	
	//--ZkComponent--//
	public String getId() {
		return _id;
	}
	
	public ZkDesktop getZkDesktop() {
		return _parent == null ? null : ((ZkComponent)_parent).getZkDesktop();
	}

	public void setAttr(String attr, String val) {
		if ("lb".equals(attr)) {
			_label = val;
			int index = _parent.indexOf(this);
			final Image img = _parent.getImage(index);
			_parent.set(index, val, img);
		} else if ("im".equals(attr)) {
			_image = val;
			UiManager.loadImageOnThread(this, getZkDesktop().getHostURL(), getImageSrc());
		}
	}
	
	public void addCommand(Command cmd) {
		//do nothing
	}
	
	public void removeCommand(Command cmd) {
		//do nothing
	}
}
