/* B85_ZK_3861VM.java

	Purpose:

	Description:

	History:
			Mon Mar 12 14:09:06 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.impl.BinderUtil;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.tracker.Tracker;
import org.zkoss.bind.sys.tracker.TrackerNode;
import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;

public class B86_ZK_4029VM {
	int counter = 0;
	private ListModelList<LineItem> model = new ListModelList<>();

	@Init
	public void init() {
		initModel();
	}

	private void initModel() {
		model.add(new LineItem("Item " + ++counter));
		model.add(new LineItem("Item " + ++counter));
		model.add(new LineItem("Item " + ++counter));
		model.add(new LineItem("Item " + ++counter));
	}

	@Command
	public void replaceItems() {
		model.clear();
		initModel();
	}

	public String getTitleForItem(LineItem item) {
		return "Title for Item: '" + item.label + "'";
	}

	public ListModelList<LineItem> getModel() {
		return model;
	}

	public static class LineItem {
		private String label;

		public LineItem(String label) {
			this.label = label;
		}

		public String getTitle() {
			return "Title for Item: '" + this.label + "'";
		}

		public String getLabel() {
			return label;
		}
	}

	@Command
	public void checkNode(@BindingParam("comp") Component bindComponent)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Clients.log(getVMNodeDependentsSize(bindComponent) + "");
	}

	public static int getVMNodeDependentsSize(Component bindComponent)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Binder binder = BinderUtil.getBinder(bindComponent);
		Tracker tracker = ((BinderCtrl) binder).getTracker();
		final AccessibleObject acs;
		try {
			acs = Classes.getAccessibleObject(tracker.getClass(), "getAllTrackerNodes", null,
					Classes.B_GET | Classes.B_METHOD_ONLY);
			acs.setAccessible(true);
			Collection<TrackerNode> trackerNodes = (Collection<TrackerNode>) ((Method) acs).invoke(tracker);
			TrackerNode vmNode = null;
			for (TrackerNode node : trackerNodes) {
				if ("vm".equals(node.getFieldScript())) {
					vmNode = node;
					break;
				}
			}
			return vmNode.getDependents().size();
		} catch (Exception e) {
			throw e;
		}
	}
}
