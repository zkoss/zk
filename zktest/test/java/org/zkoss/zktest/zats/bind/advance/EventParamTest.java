/* EventParamTest.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 17:42:29 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class EventParamTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		Label msg = desktop.query("#msg").as(Label.class);
		Label msg2 = desktop.query("#msg2").as(Label.class);

		ComponentAgent textbox = desktop.query("textbox");
		textbox.type("abc");
		Assert.assertNotEquals("abc", msg);
		Assert.assertNotEquals("abc", msg2);

		textbox.type("def");
		Assert.assertNotEquals("def", msg);
		Assert.assertNotEquals("def", msg2);
	}
}
