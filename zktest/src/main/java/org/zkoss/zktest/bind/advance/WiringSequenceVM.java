/* WiringSequenceVM.java

		Purpose:
		
		Description:
		
		History:
				Thu May 06 12:42:19 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;

@VariableResolver({ WiringSequenceVM.WireVariableResolver.class})
public class WiringSequenceVM {

	@WireVariable
	private Page page;

	public Page getPage() {
		return page;
	}

	public static class WireVariableResolver implements org.zkoss.xel.VariableResolver {
		public Object resolveVariable(String name) throws XelException {
			if ("page".equals(name)) {
				Clients.log("The variable resolver annotated registered with the VariableResolver annotation.");
				return Executions.getCurrent().getDesktop().getFirstPage();
			}
			return null;
		}
	}
}
