/* B96_ZK_5079VM.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 20 14:22:36 CST 2022, Created by jameschu

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.io.Serializable;

import org.zkoss.bind.annotation.Command;
import org.zkoss.zul.ListModelList;

/**
 * @author jameschu
 */
public class B96_ZK_5079VM implements Serializable {
	private ListModelList<String> model = new ListModelList();

	public B96_ZK_5079VM() {
		for (int i = 0; i < 5; i++) {
			model.add("item " + i);
		}
	}

	@Command
	public void del() {
		model.remove(0);
	}

	public ListModelList getModel() {
		return model;
	}
}
