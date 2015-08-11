/* F80_ZK_2831.java

	Purpose:
		
	Description:
		
	History:
		Fri Jul 31 11:09:38 CST 2015, Created by chunfu

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.xel.Evaluators;

/**
 * 
 * @author chunfu
 */
public class F80_ZK_2831 {
	String title = "deferred evaluation";
	String command = "";

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	@Command
	@NotifyChange("title")
	public void updateTitle() {
		this.title = "new title";
	}

	@Command
	@NotifyChange("command")
	public void evaluateEL(@ContextParam(ContextType.BINDER) Binder binder,
		@ContextParam(ContextType.PAGE) Page page, @ContextParam(ContextType.COMPONENT) Component comp) {
		Object o = Evaluators.evaluate(binder.getEvaluatorX(), comp, "#{abc += ' in command'}", String.class);
		command = o.toString();
	}
}
