/* WireFromImplicitObjectTest.java

		Purpose:
		
		Description:
		
		History:
				Wed May 05 17:35:46 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class WireFromImplicitObjectTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		Assertions.assertNotEquals("", desktop.query("#page1").as(Label.class).getValue());
		Assertions.assertNotEquals("", desktop.query("#desktop1").as(Label.class).getValue());
		Assertions.assertNotEquals("", desktop.query("#sess").as(Label.class).getValue());
		Assertions.assertNotEquals("", desktop.query("#wapp").as(Label.class).getValue());
		Assertions.assertNotEquals("", desktop.query("#desktopScope").as(Label.class).getValue());
	}
}
