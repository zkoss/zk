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
package org.zkoss.zkmob.impl;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;

import org.zkoss.zkmob.Itemable;
import org.zkoss.zkmob.ZkComponent;


/**
 * ZK Form.
 * @author henrichen
 *
 */
public class ZkForm extends Form implements ZkComponent {
	private String _id;
	private Zk _zk;
	
	public ZkForm(Zk zk, String id, String  title) {
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
	
	public int append(Item item) {
		final int j = super.append(item);
		((Itemable)item).setForm(this);
		return j;
	}
	
	public void insert(int itemNum, Item item) {
		super.insert(itemNum, item);
		((Itemable)item).setForm(this);
	}
	
	//--ZkComponent--//
	public String getId() {
		return _id;
	}
	
	public Zk getZk() {
		return _zk;
	}

	public void setAttr(String attr, String val) {
		if ("visibility".equals(attr)) {
			if ("true".equals(val)) {
				_zk.setCurrent(this);
			}
		}
	}
}
