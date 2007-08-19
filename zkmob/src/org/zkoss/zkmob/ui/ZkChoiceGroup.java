/* ListableChoiceGroup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 22, 2007 9:40:16 AM, Created by henrichen
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

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;

import org.zkoss.zkmob.Imageable;
import org.zkoss.zkmob.Itemable;
import org.zkoss.zkmob.Listable;
import org.zkoss.zkmob.UiManager;
import org.zkoss.zkmob.ZkComponent;


/**
 * Implement Listable ChoiceGroup.
 * 
 * @author henrichen
 *
 */
public class ZkChoiceGroup extends ChoiceGroup implements Listable, ZkComponent, Itemable {
	private String _id;
	private Boolean _onSelect;
	private ZkDesktop _zk;
	private ZkForm _form;
	private Vector _listitems = new Vector(32);
	private boolean _handlekid;
	
	public ZkChoiceGroup(ZkDesktop zk, String id, String title, int choiceType, Boolean onSelect) {
		super(title, choiceType);
		_id = id;
		_zk = zk;
		_onSelect = onSelect;
	}
	
	public Vector getItems() {
		return _listitems;
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
				UiManager.loadImageOnThread(comp,li.getZkDesktop().getHostURL(), li.getZkDesktop().getPathURL(), comp.getImageSrc());
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
				li.setParent(this);				UiManager.loadImageOnThread(comp, li.getZkDesktop().getHostURL(), li.getZkDesktop().getPathURL(), comp.getImageSrc());
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

	public Boolean getOnSelect() {
		return _onSelect;
	}

	//--ZkComponent--//
	public String getId() {
		return _id;
	}
	
	public ZkComponent getParent() {
		return (ZkComponent) _form;
	}
	
	public void setParent(ZkComponent parent) {
		if (_form != parent) { //yes, !=, not !equals
			if (_form != null) {
				_form.removeItem(this);
			}
			_form = (ZkForm) parent;
			ZkDesktop newzk = null;
			if (_form != null) {
				_form.appendChild(this);
				newzk = _form.getZkDesktop();
			}
			if (_zk != newzk) {
				_zk = newzk;
			}
		}
	}
	
	public ZkDesktop getZkDesktop() {
		return _zk;
	}

	public void setAttr(String attr, String val) {
	}
	
	//--Itemable--//
	public Form getForm() {
		return _form;
	}
}
