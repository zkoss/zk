/* B01194VM1.java

	Purpose:
		
	Description:
		
	History:
		Jun 13, 2012, Created by Ian Tsai(Zanyking)

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under ZOL in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Default;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

/**
 * @author Ian Y.T Tsai(zanyking)
 * 
 */
public class B01194NestedVMInit {

	public static class VM2 {
		private String name;
		private String desc;

		public VM2(String name, String desc) {
			super();
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

	@Wire
	private Label myLbl;
	private VM2 innerVm;

	@Init
	public void doInit(@BindingParam("type")@Default("user") String type,
			@ContextParam(ContextType.VIEW) Component self) {

		Selectors.wireComponents(self, this, false);

		if ("admin".equals(type)) {
			innerVm = new VM2("Ian", " is an Admin");
		} else if ("user".equals(type)) {
			innerVm = new VM2("Peter", "is a User");
		}

		myLbl.setValue(innerVm.getDesc());
	}
	
	@AfterCompose
	public void doAfterCompose(){
		
	}

	public VM2 getInnerVm() {
		return innerVm;
	}

	public void setInnerVm(VM2 innerVm) {
		this.innerVm = innerVm;
	}

}
