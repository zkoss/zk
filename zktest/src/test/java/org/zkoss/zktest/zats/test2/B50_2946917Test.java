/* B50_2946917Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 20 17:54:12 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class B50_2946917Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent main = desktop.query("#main");
		ComponentAgent textbox = desktop.query("textbox");
		textbox.type("");
		Assertions.assertEquals("validate 1", main.getLastChild().as(Label.class).getValue());

		textbox.type("");
		Assertions.assertEquals("validate 1", main.getLastChild().as(Label.class).getValue());

		textbox.type("zk");
		Assertions.assertEquals("validate 2", main.getLastChild().as(Label.class).getValue());
	}
}
