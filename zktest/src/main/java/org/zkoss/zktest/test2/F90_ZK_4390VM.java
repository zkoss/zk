/* F90_ZK_4390VM.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 24 12:26:36 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.NotifyCommand;
import org.zkoss.bind.annotation.ToClientCommand;
import org.zkoss.bind.annotation.ToServerCommand;

/**
 * @author jameschu
 */
@NotifyCommand(value="toClient", onChange = "_vm_.message")
@ToClientCommand("toClient")
@ToServerCommand("toServer")
public class F90_ZK_4390VM {
	String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Command
	@NotifyChange("message")
	public void toServer(@BindingParam("foo") MyFoo foo, @BindingParam("bar") MyBar bar) {
		message = foo.getFoo() + " " + bar.getBar();

	}
	public static class MyFoo {
		private String foo;
		public void setFoo(String foo) { this.foo = foo;}
		public String getFoo() { return this.foo;}
	}

	public static class MyBar {
		private String bar;
		public void setBar(String bar) { this.bar = bar;}
		public String getBar() {return this.bar;}
	}
}
