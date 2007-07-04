/* ZkGauge.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 29, 2007 3:23:13 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.ui;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;

import org.zkoss.zkmob.Itemable;
import org.zkoss.zkmob.ZkComponent;


/**
 * @author henrichen
 *
 */
public class ZkGauge extends Gauge implements ZkComponent, Itemable {
	private String _id;
	private ZkDesktop _zk;
	private ZkForm _form;
	
	public ZkGauge(ZkDesktop zk, String id, String label, boolean interactive, int maxValue, int initialValue) {
		super(label, interactive, maxValue, initialValue);
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
		if (_form != parent) { //yes, !=, not !equals
			_form = (ZkForm) parent;
			_form.appendChild(this);
		}
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
}
