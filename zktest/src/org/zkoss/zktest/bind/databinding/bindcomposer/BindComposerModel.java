/* BindComposerModel.java
	Purpose:

	Description:

	History:
		Tue May 04 11:10:38 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.databinding.bindcomposer;

import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jameschu
 */
@VariableResolver(BindComposerModel.VariableResolver.class)
public class BindComposerModel {
	private String msg = "123";

	@WireVariable("0")
	private String var;

	public String getMsg() {
		return msg;
	}

	@GlobalCommand
	public void doGlobalCommand() {
		Clients.log("doGlobalCommand called");
	}

	public String getVar() {
		return var;
	}

	public static class VariableResolver implements org.zkoss.xel.VariableResolver {
		public Object resolveVariable(String name) throws XelException {
			return name + name;
		}
	}
}
