/* F86_ZK_3986.java

	Purpose:
		
	Description:
		
	History:
		Mon Sep 10 14:09:46 CST 2018, Created by wenninghsu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Timer;

/**
 * 
 * @author wenninghsu
 */
public class F86_ZK_3986 {

	private ListModelList<String> model = new ListModelList<>();

	private long id = 0;

	private boolean removeStart;

	@Init
	public void init(){
		model.add("aaa");
		model.add("bbb");
	}

	@Command
	public void update(@BindingParam("comp") Timer comp){
		if (removeStart) {
			model.removeRange((int) id, (int) id + 2);
			if (model.size() == 2) {
				comp.stop();
			}
			return;
		}

		if (id < 6) {
			model.add("item" + id);
			model.add("item" + (id + 1));
			id = id + 2;
		} else {
			id = 2;
			removeStart = true;
			model.removeRange((int) id, (int) id + 2);
		}

	}

	public ListModelList<String> getModel() {
		return model;
	}
}
