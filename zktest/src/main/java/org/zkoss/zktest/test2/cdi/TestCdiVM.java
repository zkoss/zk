/* TestCdiVM.java

	Purpose:
		
	Description:
		
	History:
		Jul 12, 2012, Created by Ian Tsai(Zanyking)

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under ZOL in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.cdi;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

/**
 * @author Ian Y.T Tsai(zanyking)
 *
 */

@VariableResolver(org.zkoss.zkplus.cdi.DelegatingVariableResolver.class)
public class TestCdiVM {

	@WireVariable
	private Hello hello;
	
	private String name = "Ian";
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String message;	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	@NotifyChange("message")
	@Command
	public void sayHello(){
		message = hello.sayHello(name);
	}
	
	
}
