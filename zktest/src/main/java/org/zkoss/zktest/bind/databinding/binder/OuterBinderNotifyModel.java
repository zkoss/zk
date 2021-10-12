/* OuterBinderNotifyModel.java
	Purpose:

	Description:

	History:
		Tue May 04 11:10:38 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.databinding.binder;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author jameschu
 */
public class OuterBinderNotifyModel {
	private Map<String, String> msgMap = new HashMap<>();

	@Init
	public void init() {
		msgMap.put("msg", "msg");
	}

	public Map getMsgMap() {
		return msgMap;
	}

	@Command
	@NotifyChange("msgMap")
	public void refresh() {
	}

	@Command
	public void addWithoutNotify() {
		msgMap.put("msg", msgMap.get("msg") + 1);
	}

	@Command
	public void addAndNotify() {
		msgMap.put("msg", msgMap.get("msg") + 1);
		BindUtils.postNotifyChange(null, null, msgMap, ".");
	}
}
