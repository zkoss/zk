/* B96_ZK_4905VM.java

	Purpose:
		
	Description:
		
	History:
		Fri May 21 17:25:26 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModelList;

/**
 * @author rudyhuang
 */
public class B96_ZK_4905VM {
	private ListModelList<String> model = new ListModelList<>();

	public ListModelList<String> getModel() {
		return model;
	}

	@Init
	public void init() {
		model.add("foo");
		model.add("bar");
	}

	@Command
	public void doFilter() {
		model.clear();
		model.add("beep");
		model.add("bop");
	}
}
