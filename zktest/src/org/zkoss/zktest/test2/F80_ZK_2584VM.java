/* F80_ZK_2584VM.java

	Purpose:
		
	Description:
		
	History:
		12:30 PM 8/14/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.NotifyCommand;
import org.zkoss.bind.annotation.ToClientCommand;
import org.zkoss.bind.annotation.ToServerCommand;

/**
 * @author jumperchen
 */
@NotifyCommand(value="toClient", onChange = "_vm_.message")
@ToClientCommand("toClient")
@ToServerCommand("toServer")
public class F80_ZK_2584VM {
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
