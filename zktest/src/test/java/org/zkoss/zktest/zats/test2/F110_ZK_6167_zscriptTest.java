/* F110_ZK_6167_zscriptTest.java

		Purpose:
				
		Description:
				
		History:
				Sun Sep 27 19:54:58 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import bsh.Capabilities;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author peakerlee
 */
public class F110_ZK_6167_zscriptTest extends ZATSTestCase {
	@Test
	public void test() throws Exception {
		// BeanShell's accessibility is JVM-wide: start with it off whatever an earlier test declared, and restore it after
		final boolean accessibility = Capabilities.haveAccessibility();
		Capabilities.setAccessibility(false);
		try {
			assertJdkInternalCalls();

			// a class declared in zscript turns BeanShell's accessibility on for the whole JVM
			DesktopAgent desktop = connect("/test2/F110-ZK-6167-zscript-class.zul");
			assertEquals("declared", desktop.query("#declared").as(Label.class).getValue());
			assertJdkInternalCalls();
		} finally {
			Capabilities.setAccessibility(accessibility);
		}
	}

	@Test
	public void testGroovy() {
		// ZK-6167: Groovy 5.0.0-alpha-1 fails this page only on JDK 22 or later; on JDK 17 it passes either way
		DesktopAgent desktop = connect("/test2/F110-ZK-6167-zscript-groovy.zul");
		assertEquals("28800000", desktop.query("#offset").as(Label.class).getValue());
		assertEquals("true", desktop.query("#list").as(Label.class).getValue());
		assertEquals("k", desktop.query("#entry").as(Label.class).getValue());
		assertEquals("Label", desktop.query("#simpleName").as(Label.class).getValue());
	}

	private void assertJdkInternalCalls() {
		DesktopAgent desktop = connect("/test2/F110-ZK-6167-zscript.zul");
		assertEquals("28800000", desktop.query("#offset").as(Label.class).getValue());
		assertEquals("b", desktop.query("#tag").as(Label.class).getValue());
		assertEquals("x", desktop.query("#next").as(Label.class).getValue());
		assertEquals("k", desktop.query("#key").as(Label.class).getValue());
		assertEquals("28800000", desktop.query("#win #winOffset").as(Label.class).getValue());
		assertEquals("x", desktop.query("#win #winNext").as(Label.class).getValue());
	}
}
