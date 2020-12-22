package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;

public class B95_ZK_4749VM {
	private MyBean mybean = new MyBean();

	@Command
	public void prefill(@BindingParam("mybeanForm") MyBean mybeanForm) {
		mybeanForm.setProp1("test1");
		mybeanForm.setProp2("test2");
		mybeanForm.setProp3("test3");
		BindUtils.postNotifyChange(mybeanForm, "*");
	}

	public MyBean getMybean() {
		return mybean;
	}

	public static class MyBean {
		private String prop1;
		private String prop2;
		private String prop3;

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

		public String getProp3() {
			return prop3;
		}

		public void setProp3(String prop3) {
			this.prop3 = prop3;
		}
	}
}
