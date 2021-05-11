/* MyVM.java

		Purpose:
		
		Description:
		
		History:
				Mon May 10 16:50:44 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zuti.zul.NavigationLevel;
import org.zkoss.zuti.zul.NavigationModel;

public class MyVM {
	private NavigationModel<MyObject> navModel = new NavigationModel<>();

	public MyVM() {
		navModel.put("AAA", new MyObject("AAA", "desc 1"));
		navModel.put("AAA/AAA1", new MyObject("AAA1", "desc 2"));
		navModel.put("BBB", new MyObject("BBB", "desc 3"));
		navModel.put("BBB/BBB1", new MyObject("BBB1", "desc 4"));
		navModel.put("CCC", new MyObject("CCC", "desc 5"));
	}

	public NavigationModel<MyObject> getNavModel() {
		return navModel;
	}

	@Command
	public void navTo(@BindingParam("level") NavigationLevel level,
		@BindingParam("key") String key) {
		level.navigateTo(key);
	}

	@Command
	public void navByPath(@BindingParam("path") String path) {
		this.navModel.navigateToByPath(path);
	}
}
