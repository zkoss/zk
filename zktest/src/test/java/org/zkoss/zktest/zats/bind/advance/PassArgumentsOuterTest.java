/* PassArgumentsOuterTest.java

		Purpose:
		
		Description:
		
		History:
				Mon May 10 16:06:48 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class PassArgumentsOuterTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		Assert.assertEquals("outerPageLiteralValue", desktop.query("include #innerLabel").as(Label.class).getValue());
		Assert.assertEquals("outerPageABC", desktop.query("include #innerLabel2").as(Label.class).getValue());
	}

	@Test
	public void test2() {
		DesktopAgent desktop = connect("/bind/advance/PassArgumentsOuter-2.zul");

		Assert.assertEquals("myArgument", desktop.query("include #innerLabel").as(Label.class).getValue());
		Assert.assertEquals("myArgument", desktop.query("include #innerLabel2").as(Label.class).getValue());

		desktop.query("button").click();
		// shall not change
		Assert.assertEquals("myArgument", desktop.query("include #innerLabel").as(Label.class).getValue());
		Assert.assertEquals("myArgument", desktop.query("include #innerLabel2").as(Label.class).getValue());
	}
}
