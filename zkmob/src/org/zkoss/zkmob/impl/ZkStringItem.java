/* ZkStringItem.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 29, 2007 3:31:22 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.impl;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;

import org.zkoss.zkmob.Itemable;
import org.zkoss.zkmob.ZkComponent;
import org.zkoss.zkmob.ZkComponents;


/**
 * ZK StringItem.
 * @author henrichen
 *
 */
public class ZkStringItem extends StringItem implements ZkComponent, Itemable {
	private String _id;
	private Zk _zk;
	private Form _form;
	
	public ZkStringItem(Zk zk, String id, String label, String text) {
		super(label, text);
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
		ZkComponents.setItemAttr(this, attr, val);
		if ("tx".equals(attr)) {
			setText(val);
		}
	}

	//--Itemable--//
	public Form getForm() {
		return _form;
	}
	
	public void setForm(Form form) {
		_form = form;
	}
}
