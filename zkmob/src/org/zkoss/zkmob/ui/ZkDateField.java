/* ZkDateField.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 29, 2007 3:17:38 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.ui;

import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Form;

import org.zkoss.zkmob.Itemable;
import org.zkoss.zkmob.ZkComponent;



/**
 * DateField with id.
 * @author henrichen
 *
 */
public class ZkDateField extends DateField implements ZkComponent, Itemable {
	private String _id;
	private ZkDesktop _zk;
	private ZkForm _form;
	
	public ZkDateField(ZkDesktop zk, String id, String label, int mode) {
		super(label, mode);
		_id = id;
		_zk = zk;
	}
	
	//--ZkComponent --//
	public String getId() {
		return _id;
	}

	public ZkComponent getParent() {
		return (ZkComponent) getForm();
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
		//TODO:
	}

	//--Itemable--//
	public Form getForm() {
		return _form;
	}
}
