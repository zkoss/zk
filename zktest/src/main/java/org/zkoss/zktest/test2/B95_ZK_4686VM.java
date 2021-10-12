/* B95_ZK_4685VM.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 24 10:25:45 AM CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.bind.impl.TrackerImplEx;

public class B95_ZK_4686VM {
	private Data data = new Data("default");
	private Data data1 = new Data("default");
	private int nodeCacheSize = 0;

	@Command
	public void displayView1() {
		BindUtils.postNotifyChange(data, "template");
		data = new Data("view1");
		BindUtils.postNotifyChange(this, "data");
	}

	@Command
	public void displayDefault() {
		BindUtils.postNotifyChange(data, "template");
		data = new Data("default");
		BindUtils.postNotifyChange(this, "data");
	}

	@Command
	@NotifyChange("nodeCacheSize")
	public void showNodeCache(@ContextParam(ContextType.BINDER) Binder binder) {
		nodeCacheSize = ((TrackerImplEx) ((BinderImpl) binder).getTracker()).getEqualBeans(data).size();
	}

	public Data getData() {
		return data;
	}

	public Data getData1() {
		return data1;
	}

	public int getNodeCacheSize() {
		return nodeCacheSize;
	}

	public static class Data {
		private String template;
		public Data() {
		}

		public Data(String template) {
			this.template = template;
		}

		public String getTemplate() {
			return template;
		}

		public void setTemplate(String template) {
			this.template = template;
		}
	}
}