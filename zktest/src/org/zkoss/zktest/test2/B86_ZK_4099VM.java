/* B86_ZK_4099VM.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 25 09:44:53 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.LinkedList;
import java.util.List;

import org.zkoss.bind.annotation.AutoNotifyChange;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;

/**
 * @author rudyhuang
 */
@AutoNotifyChange
public class B86_ZK_4099VM {
	private List list;

	@Init
	public void init() {
		list = new LinkedList();
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public void set(boolean whatever) {
		// do nothing
	}

	@Command
	public void testSet() {
		set(true);
	}
}
