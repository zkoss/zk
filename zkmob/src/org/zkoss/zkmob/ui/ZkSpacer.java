/* ZkSpacer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 29, 2007 3:29:03 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.ui;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Spacer;

import org.zkoss.zkmob.Itemable;
import org.zkoss.zkmob.ZkComponent;


/**
 * ZK Spacer.
 * @author henrichen
 *
 */
public class ZkSpacer extends Spacer implements ZkComponent, Itemable {
	private String _id;
	private ZkDesktop _zk;
	private Form _form;
	
	public ZkSpacer(ZkDesktop zk, String id, int w, int h) {
		super(w, h);
		_id = id;
		_zk = zk;
	}
	
	//--ZkComponent--//
	public String getId() {
		return _id;
	}
	
	public ZkComponent getParent() {
		return (ZkComponent) getForm();
	}
	
	public void setParent(ZkComponent parent) {
		setForm((Form) parent);
	}
	
	public ZkDesktop getZkDesktop() {
		return _zk;
	}

	public void setAttr(String attr, String val) {
		//TODO:
	}

	//--Itemable--//
	public Form getForm() {
		return _form;
	}
	
	public void setForm(Form form) {
		_form = form;
	}
}
