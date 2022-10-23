/* B80_ZK_2919.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 10 10:03:53 CST 2015, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Arrays;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jameschu
 */
public class B80_ZK_2919VM {
	private String item;
	private List values = Arrays.asList("value 1", "value 2");

	public List getValues() {
		return this.values;
	}

	public String getItem() {
		return this.item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	@Command("save")
	public void save() {
		if (item == null || item.isEmpty()) {
			throw new WrongValueException("Not valid");
		}
	}

	@Command("fake")
	public void fake() {
	}

	@Command("showValue")
	public void showValue() {
		Clients.alert(item);
	}
}