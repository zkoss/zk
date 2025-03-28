/* B102_ZK_5889ViewModel.java

	Purpose:

	Description:

	History:
		Fri Mar 28 11:51:21 CST 2025, Created by jameschu

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.util.Clients;

import java.util.*;

public class B102_ZK_5889ViewModel {

	private B102_ZK_5889Pojo myPojo;

	public List<B102_ZK_5889ChildPojo> getChildren() {
		return children;
	}

	public void setChildren(List<B102_ZK_5889ChildPojo> children) {
		this.children = children;
	}

	private List<B102_ZK_5889ChildPojo> children = new ArrayList<>();

	@Init
	public void init() {
		myPojo = new B102_ZK_5889Pojo();
		myPojo.setChild(new B102_ZK_5889ChildPojo());

		children.add(new B102_ZK_5889ChildPojo("child1", 1));
		children.add(new B102_ZK_5889ChildPojo("child2", 2));
		children.add(new B102_ZK_5889ChildPojo("child3", 3));
		children.add(new B102_ZK_5889ChildPojo("child4", 4));
		children.add(new B102_ZK_5889ChildPojo("child5", 5));
	}


	@Command
	public void cancel() {
		Clients.log("cancelled: " + myPojo.getDebugString());
	}

	@Command
	public void save() {
		Clients.log("saved: " + myPojo.getDebugString());
	}


	public B102_ZK_5889Pojo getMyPojo() {
		return myPojo;
	}

}

