/* B95_ZK_4685VM.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 24 10:25:45 AM CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.Form;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.zk.ui.util.Clients;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.bind.sys.SaveBinding;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;

import java.util.Set;

public class B95_ZK_4722VM {
	private MyBean mybean = new MyBean();
	private int counter = 0;
	private int result = 0;

	@Command
	@NotifyChange("result")
	public void save(@ContextParam(ContextType.BINDER) BinderImpl binder, @SelectorParam("#formDiv") Component formComp) {
		counter++;
		BindUtils.postNotifyChange(this, "counter");
		Clients.log("Counter: " + counter);
		Set<SaveBinding> bindings = binder.getFormAssociatedSaveBindings(formComp);
		Clients.log("FormAssociatedSaveBindings: " + bindings.size());
		result = bindings.size();
		Clients.log(bindings.toString());
	}

	public int getCounter() {
		return counter;
	}

	public MyBean getMybean() {
		return mybean;
	}

	public int getResult() {
		return result;
	}

	public static class MyBean {
		private String prop1;
		public String getProp1() {
			return prop1;
		}

		public void setProp1(String prop1) {
			this.prop1 = prop1;
		}
	}

	public static class InnerVM {

	}
}
