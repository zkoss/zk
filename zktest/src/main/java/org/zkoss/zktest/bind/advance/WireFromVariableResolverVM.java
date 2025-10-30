/* WireFromVariableResolverVM.java

		Purpose:
		
		Description:
		
		History:
				Thu May 06 10:24:07 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

@VariableResolver({WireFromVariableResolverVM.WireVariableResolver1.class, WireFromVariableResolverVM.WireVariableResolver2.class})
public class WireFromVariableResolverVM {

	@WireVariable
	private String var;

	private String var2;

	public String getVar() {
		return var;
	}

	public String getVar2() {
		return var2;
	}

	@WireVariable
	public void setVar2(String v2) {
		var2 = v2;
	}

	public static class WireVariableResolver1 implements org.zkoss.xel.VariableResolver {
		public Object resolveVariable(String name) throws XelException {
			if ("var".equals(name))
				return "Resolver1";
			return null;
		}
	}

	public static class WireVariableResolver2 implements org.zkoss.xel.VariableResolver {
		public Object resolveVariable(String name) throws XelException {
			if ("var2".equals(name))
				return "Resolver2";
			return null;
		}
	}
}
