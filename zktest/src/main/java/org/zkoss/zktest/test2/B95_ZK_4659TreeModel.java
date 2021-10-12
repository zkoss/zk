/* B95_ZK_4659TreeModel.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 24 16:28:36 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.lang.Threads;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.AbstractTreeModel;

/**
 * @author rudyhuang
 */
public class B95_ZK_4659TreeModel extends AbstractTreeModel<String> {
	public B95_ZK_4659TreeModel(String root) {
		super(root);
	}

	public boolean isLeaf(String node) {
		return node.contains(".");
	}

	public String getChild(String parent, int nth) {
		Execution exec = Executions.getCurrent();
		if (!exec.hasAttribute("delayOnce")) {
			Threads.sleep(2000);
			exec.setAttribute("delayOnce", "dummy");
		}
		return parent + "." + nth;
	}

	public int getChildCount(String parent) {
		return isLeaf(parent) ? 0 : 500;
	}
}
