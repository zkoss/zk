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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Default;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.impl.AnnotateBinderHelper;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.metainfo.Annotation;
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
	private Label headerNameLb;
	private VM2 innerVm;

	@Init
	public void doInit(@BindingParam("type")@Default("user") String type,
			@ContextParam(ContextType.VIEW) Component self) {

		if ("admin".equals(type)) {
			innerVm = new VM2("Ian", "is an Admin");
		} else if ("user".equals(type)) {
			innerVm = new VM2("Peter", "is a User");
		}

		
	}
	
	@AfterCompose
	public void doAfterCompose(@BindingParam("type")@Default("user") String type,
			@ContextParam(ContextType.VIEW) Component self, 
			@ContextParam(ContextType.BINDER) Binder binder){
		
		Selectors.wireComponents(self, this, false);
		HashMap<String, String[]> annotAttrs = new HashMap<String, String[]>();
		annotAttrs.put("value", new String[]{"vm.innerVm.name"});
		headerNameLb.addAnnotation("value", "load", annotAttrs);
		new AnnotateBinderHelper(binder).initComponentBindings(headerNameLb);
	}
	
	public VM2 getInnerVm() {
		return innerVm;
	}

	public void setInnerVm(VM2 innerVm) {
		this.innerVm = innerVm;
	}

}
