/* F90_ZK_4392VM.java

	Purpose:
		
	Description:
		
	History:
		Mon Oct 7 12:26:36 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.TreeModel;

/**
 * @author jameschu
 */
public class F90_ZK_4392VM {
	DefaultTreeModel m1 = new F90_ZK_4392Model("A");
	DefaultTreeModel m2 = new F90_ZK_4392BeanModel("bean");
	boolean disabled = false;

	@Init
	public void init() {
	}

	public DefaultTreeModel getModel1() {
		return m1;
	}

	public DefaultTreeModel getModel2() {
		return m2;
	}

	public boolean getDisabled() {
		return disabled;
	}

	@Command
	public void changeSelection() {
		m1.addToSelection(m1.getChild(new int[]{1,1}));
	}

	@Command
	public void clearSelection() {
		m1.clearSelection();
	}

	@Command
	@NotifyChange("disabled")
	public void toggleDisabled() {
		disabled = !disabled;
	}
}
