/* WireFromVariableResolverTest.java

		Purpose:
		
		Description:
		
		History:
				Thu May 06 10:47:17 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class WireFromVariableResolverTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		Assertions.assertEquals("Resolver1", desktop.query("#l1").as(Label.class).getValue());
		Assertions.assertEquals("Resolver2", desktop.query("#l2").as(Label.class).getValue());
	}
}