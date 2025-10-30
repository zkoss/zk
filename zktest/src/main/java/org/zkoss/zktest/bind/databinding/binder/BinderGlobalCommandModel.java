/* BinderGlobalCommandModel.java
	Purpose:

	Description:

	History:
		Tue May 04 11:10:38 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.databinding.binder;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.AnnotateBinder;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jameschu
 */
public class BinderGlobalCommandModel {
	Binder binder;

	@GlobalCommand
	public void globalCmdInner(@BindingParam("arg1") int arg1, @BindingParam("arg2") String arg2) {
		Clients.log("GlobalCommand called > arg1: " + arg1 + ", arg2: " + arg2);
	}

	@GlobalCommand
	public void globalCmdInnerMyQueue(@BindingParam("arg1") int arg1, @BindingParam("arg2") String arg2) {
		Clients.log("GlobalCommand called > arg1: " + arg1 + ", arg2: " + arg2);
	}

	@Command
	public void postGlobalCmdOuter() {
		Map<String, Object> args = new HashMap<>();
		args.put("arg1", 1);
		args.put("arg2", "outer");
		BindUtils.postGlobalCommand(null, null, "globalCmdOuter", args);
	}

	@Command
	public void postGlobalCmdOuterMyQueue() {
		Map<String, Object> args = new HashMap<>();
		args.put("arg1", 2);
		args.put("arg2", "outerMyQueue");
		BindUtils.postGlobalCommand("myQueue", null, "globalCmdOuter", args);
	}

	@Command
	public void postGlobalCmdInner() {
		Map<String, Object> args = new HashMap<>();
		args.put("arg1", 3);
		args.put("arg2", "inner");
		BindUtils.postGlobalCommand(null, null, "globalCmdInner", args);
	}

	@Command
	public void postGlobalCmdInnerMyQueue() {
		Map<String, Object> args = new HashMap<>();
		args.put("arg1", 4);
		args.put("arg2", "innerMyQueue");
		BindUtils.postGlobalCommand("myQueue", null, "globalCmdInnerMyQueue", args);
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
