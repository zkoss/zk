/* WireZULVariableResolver.java

		Purpose:
		
		Description:
		
		History:
				Thu May 06 14:06:20 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;

public class WireZULVariableResolver implements org.zkoss.xel.VariableResolver {
	public Object resolveVariable(String name) throws XelException {
		if ("page".equals(name)) {
			Clients.log("The variable resolver defined in the ZUML document.");
			return Executions.getCurrent().getDesktop().getFirstPage();
		}
		return null;
	}
}