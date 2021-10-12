/* F01231AfterComposeVM.java

	Purpose:
		
	Description:
		
	History:
		Aug 28, 2012, Created by Ian Tsai(Zanyking)

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under ZOL in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zktest.bind.issue;

import java.util.HashMap;

import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Default;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

/**
 * @author Ian Y.T Tsai(zanyking)
 *
 */
public class F01231AfterComposeVM {
	
	private String name;
	private String desc;

	@Wire
	private Label headerLb;
	
	@AfterCompose
	public void doAfterCompose(
			@BindingParam("label") String label,
			@BindingParam("name") String name,
			@BindingParam("desc") String desc,
			@ContextParam(ContextType.VIEW) Window self){
		// test wired component
		Selectors.wireComponents(self, this, false);
		headerLb.setValue(label);
		// test Binding Param
		self.setTitle("AAAA");
		//test binding param
		this.name = name;
		this.desc = desc;
	}
	
	

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
}
