/* F80_ZK_2822Composer.java

	Purpose:
		
	Description:
		
	History:
		12:13 PM 8/14/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.annotation.Command;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

/**
 * @author jumperchen
 */
public class F80_ZK_2822Composer extends SelectorComposer<Window> {

	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
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
	@Command
	public void clientCommand(MyFoo foo, MyBar bar) {
		Clients.log(foo.getFoo() + " " + bar.getBar());
	}
}
