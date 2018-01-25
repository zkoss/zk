package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class B85_ZK_3812VM {
	private List<MyBeanList> myContainer;
	private MyBeanList myBeans;

	@Init
	public void init() {
		List<String> data = new ArrayList<String>();
		for (int i = 1; i <= 10; i++)
			data.add("d" + i);
		myBeans = new MyBeanList(data);
		myContainer = new ArrayList();
		myContainer.add(myBeans);
	}

	public List<MyBeanList> getMyContainer() {
		return myContainer;
	}

	public MyBeanList getMyBeans() {
		return myBeans;
	}

	@Command
	public void cmd1() {
		myBeans.removeFirst();
		Clients.log("model size: " + myBeans.getBeanList().size());
	}

	public class MyBeanList {
		private List<String> beanList;

		public List<String> getBeanList() {
			return beanList;
		}

		public MyBeanList(List<String> beanList) {
			super();
			this.beanList = beanList;
		}

		public void removeFirst() {
			beanList.remove(0);
			BindUtils.postNotifyChange(null, null, this, "beanList");
		}
	}
}
