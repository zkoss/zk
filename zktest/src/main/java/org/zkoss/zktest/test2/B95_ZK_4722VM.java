/* B95_ZK_4685VM.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 24 10:25:45 AM CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Date;
import java.util.Set;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.SelectorParam;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.bind.sys.SaveBinding;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;

public class B95_ZK_4722VM {
	private MyBean mybean = new MyBean(new MySubBean(new Date()));
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

	@Command
	public void show(@ContextParam(ContextType.BINDER) BinderImpl binder, @SelectorParam("#formDiv") Component formComp) {
		Set<SaveBinding> bindings = binder.getFormAssociatedSaveBindings(formComp);
		Clients.log("FormAssociatedSaveBindings: " + bindings.size());
		Clients.log(bindings.toString());
	}

	@Command
	public void validate() {
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
		private String prop2;
		private MySubBean subBean;

		public MyBean() {
		}

		public MyBean(MySubBean subBean) {
			this.subBean = subBean;
		}

		public String getProp1() {
			return prop1;
		}

		public void setProp1(String prop1) {
			this.prop1 = prop1;
		}

		public String getProp2() {
			return prop2;
		}

		public void setProp2(String prop2) {
			this.prop2 = prop2;
		}

		public MySubBean getSubBean() {
			return subBean;
		}

		public void setSubBean(MySubBean subBean) {
			this.subBean = subBean;
		}
	}

	public static class MySubBean {
		private String prop1;
		private Date prop2;

		public MySubBean() {
		}

		public MySubBean(Date prop2) {
			this.prop2 = prop2;
		}

		public String getProp1() {
			return prop1;
		}

		public void setProp1(String prop1) {
			this.prop1 = prop1;
		}

		public Date getProp2() {
			return prop2;
		}

		public void setProp2(Date prop2) {
			this.prop2 = prop2;
		}
	}

	public static class InnerVM {
		@Command
		public void show(@ContextParam(ContextType.BINDER) BinderImpl binder, @SelectorParam("#formDiv") Component formComp) {
			Set<SaveBinding> bindings = binder.getFormAssociatedSaveBindings(formComp);
			Clients.log("FormAssociatedSaveBindings: " + bindings.size());
			Clients.log(bindings.toString());
		}
	}
}
