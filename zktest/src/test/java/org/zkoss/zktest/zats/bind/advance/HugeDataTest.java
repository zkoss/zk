/* HugeDataTest.java

		Purpose:
		
		Description:
		
		History:
				Mon May 10 14:18:44 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Listbox;

public class HugeDataTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		Assertions.assertEquals(1000, desktop.query("listbox").as(Listbox.class).getModel().getSize());
		Assertions.assertNotEquals(0, desktop.queryAll("listitem").size());
	}
}
