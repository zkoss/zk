/* F110_ZK_6167Test.java

		Purpose:
				
		Description:
				
		History:
				Sun Sep 27 19:54:57 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class F110_ZK_6167Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		for (String id : new String[] {"elProp", "elCall", "mvProp", "mvCall"})
			assertEquals("28800000", desktop.query("window #" + id).as(Label.class).getValue(), id);
		assertEquals("r", desktop.query("window #elXml").as(Label.class).getValue());
		assertEquals("r", desktop.query("window #mvXml").as(Label.class).getValue());

		desktop.query("window #box").type("3600000");
		assertEquals("3600000", desktop.query("window #mvProp").as(Label.class).getValue());
	}

	@Test
	public void testDefaultEvaluator() {
		DesktopAgent desktop = connect("/test2/F110-ZK-6167-default-el.zul");
		assertEquals("28800000", desktop.query("window #elProp").as(Label.class).getValue());
		assertEquals("r", desktop.query("window #elXml").as(Label.class).getValue());
	}

	@Test
	public void testMvelEvaluator() {
		// mvel14 1.2.21 cannot start on JDK 21 or later: it loads java.lang.Compiler
		assumeTrue(Runtime.version().feature() < 21, "mvel14 needs JDK 17 to 20");
		DesktopAgent desktop = connect("/test2/F110-ZK-6167-mvel.zul");
		assertEquals("3", desktop.query("window #sum").as(Label.class).getValue());
		assertEquals("mvel", desktop.query("window #title").as(Label.class).getValue());
	}
}
