/* B86_ZK_4160VM.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 14 17:59:06 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.zul.ListModelList;

/**
 * @author rudyhuang
 */
public class B86_ZK_4160VM {
	private static int count = 0;
	private ListModelList<String> items = new ListModelList<>();

	public ListModelList<String> getItems() {
		return items;
	}

	@Command
	public void add() {
		count++;
		items.add("test" + count);
	}
}
