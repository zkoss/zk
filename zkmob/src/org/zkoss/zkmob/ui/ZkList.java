/* ListableList.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 22, 2007 9:34:16 AM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.ui;

import java.util.Enumeration;
import java.util.Vector;

import org.zkoss.zkmob.Listable;
import org.zkoss.zkmob.UiManager;
import org.zkoss.zkmob.ZkComponent;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

/**
 * Implement Listable List.
 * 
 * @author henrichen
 */
public class ZkList extends List implements Listable, ZkComponent {
	private String _id;
	private ZkDesktop _zk;
	private boolean _handlekid;
	private Vector _listitems = new Vector(32);
	
	public ZkList(ZkDesktop zk, String id, String title, int listType) {
		super(title, listType);
		_id = id;
		_zk = zk;
	}
	
	//--Listable--//
	public void appendChild(ZkComponent li) {
		if (_handlekid) { //avoid dead loop
			return;
		}
		try {
			_handlekid = true;
			if (li instanceof ZkListItem) {
				final ZkListItem comp = (ZkListItem) li;
				super.append(comp.getLabel(), null);
				_listitems.addElement(li);
				li.setParent(this);
				UiManager.loadImageOnThread(comp, li.getZkDesktop().getHostURL(), li.getZkDesktop().getPathURL(),  comp.getImageSrc());
			} else {
				addCommand((ZkCommand) li);
				li.setParent(this);
			}
		} finally {
			_handlekid = false;
		}
	}

	public void insertChild(int index, ZkComponent li) {
		try {
			_handlekid = true;
			if (li instanceof ZkListItem) {
				final ZkListItem comp = (ZkListItem) li;
				super.insert(index, comp.getLabel(), null);
				_listitems.insertElementAt(li, index);
				li.setParent(this);
				UiManager.loadImageOnThread(comp, li.getZkDesktop().getHostURL(), li.getZkDesktop().getPathURL(), comp.getImageSrc());
			} else {
				addCommand((ZkCommand) li);
				li.setParent(this);
			}
		} finally {
			_handlekid = false;
		}
	}

	public int indexOf(ZkComponent li) {
		return _listitems.indexOf(li);
	}
	
	public void delete(int index) {
		super.delete(index);
		_listitems.removeElementAt(index);
	}

	public void superDelete(int index) {
		super.delete(index);
	}
	
	//--ZkComponent--//
	public String getId() {
		return _id;
	}
	
	public void setParent(ZkComponent parent) {
		//do nothing
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
