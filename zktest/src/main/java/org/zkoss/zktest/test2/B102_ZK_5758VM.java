package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;

public class B102_ZK_5758VM {
	MyBean lastRefreshed = null;
	MyBean myBean1 = new MyBean("label1");
	MyBean myBean2 = new MyBean("label2");
	MyBean myBean3 = new MyBean("label3");

	@Command("refreshBean")
	public void refreshBean(@BindingParam("myBean") MyBean myBean) {
		myBean.refresh();
		this.lastRefreshed = myBean;
		BindUtils.postNotifyChange(null, null, myBean, "label");
		BindUtils.postNotifyChange(null, null, this, "lastRefreshed");
	}

	public static class MyBean {
		private String label = "label";
		private int count = 0;

		public void refresh() {
			count++;
		}

		public String getLabel() {
			return String.format("%s %s %s", label, count, Math.random());
		}

		MyBean(String label) {
			this.label = label;
		}
	}

	public MyBean getLastRefreshed() {
		return lastRefreshed;
	}

	public MyBean getMyBean1() {
		return myBean1;
	}

	public MyBean getMyBean2() {
		return myBean2;
	}

	public MyBean getMyBean3() {
		return myBean3;
	}
}