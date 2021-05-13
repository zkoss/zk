/* BinderNotifyModel.java
	Purpose:

	Description:

	History:
		Tue May 04 11:10:38 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.databinding.binder;

import java.util.Map;

import org.zkoss.bind.AnnotateBinder;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;

/**
 * @author jameschu
 */
public class BinderNotifyModel {
	private Map<String, String> msgMap;
	Binder binder;

	@Init
	public void init(@BindingParam("arg1") Map arg1) {
		msgMap = arg1;
	}

	@Command
	public void updateInMyQueue() {
		BindUtils.postNotifyChange("myQueue", null, msgMap, ".");
	}

	@Command
	public void updateInDefaultQueue() {
		BindUtils.postNotifyChange(null, null, msgMap, ".");
	}

	public Map getMsgMap() {
		return msgMap;
	}

	public Binder getBinder() {
		if (binder == null) {
			binder = new MyBinder();
		}
		return binder;
	}

	public class MyBinder extends AnnotateBinder {
		public String getName() {
			return "Inner XYZ";
		}
	}
}
