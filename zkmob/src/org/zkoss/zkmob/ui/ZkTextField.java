/* ZkTextField.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 29, 2007 3:43:22 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.ui;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import org.zkoss.zkmob.Inputable;
import org.zkoss.zkmob.Itemable;
import org.zkoss.zkmob.UiManager;
import org.zkoss.zkmob.ZkComponent;


/**
 * @author henrichen
 *
 */
public class ZkTextField extends TextField implements ZkComponent, Inputable, Itemable {
	private String _id;
	private Zk _zk;
	private Boolean _onChange; //null mean no such event required, t means asap, f means !asap
	private Boolean _onChanging; //null mean no such event required, t means asap, f means !asap
	private Form _form;
	
	public ZkTextField(Zk zk, String id, String label, String text, int maxSize, int constraints, Boolean onChange, Boolean onChanging) {
		super(label, text, maxSize, constraints);
		_id = id;
		_zk = zk;
		_onChange = onChange;
		_onChanging = onChanging;
	}
	
	//--Inputable--//
	public Boolean getOnChange() {
		return _onChange;
	}
	
	public Boolean getOnChanging() {
		return _onChanging;
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
	
	public Zk getZk() {
		return _zk;
	}

	public void setAttr(String attr, String val) {
		UiManager.setItemAttr(this, attr, val);
		if ("tx".equals(attr)) {
			setString(val);
		} else if ("xs".equals(attr)) {
			setMaxSize(val == null ? 32 : Integer.parseInt(val));
		} else if ("cs".equals(attr)) {
			setConstraints(Integer.parseInt(val));
		} else if ("md".equals(attr)) { //initialInputMode
			setInitialInputMode(val); 
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
