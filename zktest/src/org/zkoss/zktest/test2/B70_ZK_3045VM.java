/* B70_ZK_3045VM.java

	Purpose:
		
	Description:
		
	History:
		9:40 AM 12/24/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.lang.reflect.Field;
import java.util.Map;

import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.bind.tracker.impl.TrackerImpl;
import org.zkoss.zul.ListModelList;

/**
 * @author jumperchen
 */
public class B70_ZK_3045VM {

	private ListModelList<Pojo> data = new ListModelList<Pojo>();

	private BinderImpl binder;

	public B70_ZK_3045VM() {
		reload();
	}

	@Init
	public void init(@ContextParam(ContextType.BINDER) Binder binder) {
		this.binder = (BinderImpl) binder;
	}

	public ListModelList<Pojo> getModel() {
		return data;
	}

	public int getComponentsCount()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		TrackerImpl tracker = (TrackerImpl) binder.getTracker();
		Field field = TrackerImpl.class.getDeclaredField("_compMap");
		field.setAccessible(true);

		Map<?, ?> map = (Map<?, ?>) field.get(tracker);

		return map.size();
	}

	@Command
	@NotifyChange("*")
	public void doMemoryLeak() {
		reload();
	}

	private void reload() {
		data.clear();
		for (int a=0; a<2; a++) {
			data.add(new Pojo("Test - "+a));
		}
	}

	public static class Pojo {
		private String data;

		public Pojo(String data) {
			this.data = data;
		}

		public void setData(String data) {
			this.data = data;
		}

		public String getData() {
			return data;
		}

	}
}
