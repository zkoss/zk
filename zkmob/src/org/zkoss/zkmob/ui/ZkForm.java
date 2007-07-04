/* ZkForm.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 29, 2007 3:20:30 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.ui;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;

import org.zkoss.zkmob.Imageable;
import org.zkoss.zkmob.Itemable;
import org.zkoss.zkmob.UiManager;
import org.zkoss.zkmob.ZkComponent;


/**
 * ZK Form.
 * @author henrichen
 *
 */
public class ZkForm extends Form implements ZkComponent {
	private String _id;
	private ZkDesktop _zk;
	private boolean _handlekid;
	
	public ZkForm(ZkDesktop zk, String id, String  title) {
		super(title);
		_id = id;
		_zk = zk;
	}
	
	public void removeItem(Item comp) {
		final int sz = size();
		for (int j = 0; j < sz; ++j) {
			final Item item = get(j);
			if (comp == item) { //found
				delete(j);
				break;
			}
		}
	}
	
	public int indexOf(Item comp) {
		final int sz = size();
		for (int j = 0; j < sz; ++j) {
			final Item item = get(j);
			if (comp == item) { //found
				return j;
			}
		}
		return -1;
	}
	
	public void appendChild(ZkComponent kid) {
		if (_handlekid) { //avoid dead loop
			return;
		}
		try {
			_handlekid = true;
			if (kid instanceof Item) {
				super.append((Item)kid);
			} else { //ZkCommand
				addCommand((ZkCommand) kid);
			}
			kid.setParent(this);
			if (kid instanceof Imageable) {
				final String hostURL = kid.getZkDesktop().getHostURL();
				final Imageable comp = (Imageable) kid;
				UiManager.loadImageOnThread(comp, hostURL, comp.getImageSrc());
			}
		} finally {
			_handlekid = false;
		}
	}
	
	public void insertChild(int itemNum, ZkComponent kid) {
		try {
			_handlekid = true;
			if (kid instanceof Item) {
				super.insert(itemNum, (Item) kid);
			} else { //ZkCommand
				addCommand((ZkCommand) kid);
			}
			kid.setParent(this);
			if (kid instanceof Imageable) {
				final String hostURL = kid.getZkDesktop().getHostURL();
				final Imageable comp = (Imageable) kid;
				UiManager.loadImageOnThread(comp, hostURL, comp.getImageSrc());
			}
		} finally {
			_handlekid = false;
		}
	}
	
	//--ZkComponent--//
	public String getId() {
		return _id;
	}
	
	public ZkDesktop getZkDesktop() {
		return _zk;
	}
	
	public void setParent(ZkComponent parent) {
		_zk = (ZkDesktop) parent;
	}
	
	public ZkComponent getParent() {
		return getZkDesktop();
	}

	public void setAttr(String attr, String val) {
		if ("visibility".equals(attr)) {
			if ("true".equals(val)) {
				_zk.setCurrent(this);
			}
		}
	}
}
