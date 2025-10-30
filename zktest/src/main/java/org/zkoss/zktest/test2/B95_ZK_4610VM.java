/* B95_ZK_4610VM.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 12 16:22:58 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Arrays;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModelList;

/**
 * @author rudyhuang
 */
public class B95_ZK_4610VM {
	private ListModelList<String> items;

	@Init
	public void init() {
		items = new ListModelList<>();
		items.addAll(Arrays.asList("Test 1", "Test 2", "Test 3"));
	}

	@Command
	public void refresh() {
		items.clear();
		items.addAll(Arrays.asList("Test 4", "Test 5", "Test 6"));
	}

	public ListModelList<String> getItems() {
		return items;
	}
}
