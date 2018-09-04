/* B86_ZK_3986VM.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep  4 12:31:38 CST 2018, Created by wenninghsu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModelList;

/**
 * 
 * @author wenninghsu
 */
public class B86_ZK_3986VM {

	private ListModelList<String> model = new ListModelList<>();
	private long id = 0;

	@Init
	public void init() {
		model.add("aaa");
		model.add("bbb");
		model.add("ccc");
		model.add("ddd");
	}

	@Command
	public void update() {
		model.add("item" + id);
		id++;
	}

	public ListModelList<String> getModel() {
		return model;
	}

}
