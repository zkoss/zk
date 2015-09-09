/* F80_ZK_2838CollectionTest.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep  2 10:04:30 CST 2015, Created by chunfu

Copyright (C)  2015 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.verifier.HierarchyVerifier;
import org.zkoss.zktest.zats.zuti.verifier.NullShadowRoot;

/**
 * 
 * @author chunfu
 */
public class F80_ZK_2838CollectionTest extends ZutiBasicTestCase {
	@Test
	public void testDropFalse() {
		DesktopAgent desktop = connect(getTestURL("F80-ZK-2838Collection.zul"));
		ComponentAgent btn1 = desktop.query("#btn1");
		ComponentAgent btn2 = desktop.query("#btn2");
		ComponentAgent btn3 = desktop.query("#btn3");

		ComponentAgent host1 = desktop.query("#host1");
		ComponentAgent host2 = desktop.query("#host2");

		assertTrue(host1.getOwner().getChildren().size() == 0);
		btn1.click();
		assertTrue(host1.getOwner().getChildren().size() == 3);
		checkVerifier(host1.getOwner(), NullShadowRoot.class, HierarchyVerifier.class);
		btn3.click();
		assertTrue(host1.getOwner().getChildren().size() == 6);
		checkVerifier(host1.getOwner(), NullShadowRoot.class, HierarchyVerifier.class);
		assertTrue(host2.getOwner().getChildren().size() == 0);
		btn2.click();
		assertTrue(host2.getOwner().getChildren().size() == 3);
		checkVerifier(host2.getOwner(), NullShadowRoot.class, HierarchyVerifier.class);
		btn3.click();
		assertTrue(host2.getOwner().getChildren().size() == 6);
		checkVerifier(host2.getOwner(), NullShadowRoot.class, HierarchyVerifier.class);
	}

	@Test
	public void testDropTrue() {
		DesktopAgent desktop = connect(getTestURL("F80-ZK-2838Collection.zul"));
		ComponentAgent btn4 = desktop.query("#btn4");
		ComponentAgent btn5 = desktop.query("#btn5");
		ComponentAgent btn6 = desktop.query("#btn6");
		ComponentAgent btn7 = desktop.query("#btn7");

		ComponentAgent host3 = desktop.query("#host3");
		ComponentAgent host4 = desktop.query("#host4");

		assertTrue(host3.getOwner().getChildren().size() == 3);
		btn4.click();
		assertTrue(host3.getOwner().getChildren().size() == 3);
		checkVerifier(host3.getOwner(), HierarchyVerifier.class);
		btn6.click();
		assertTrue(host3.getOwner().getChildren().size() == 3);
		checkVerifier(host3.getOwner(), HierarchyVerifier.class);
		try {
			btn5.click(); //should catch exception because host is not null
			fail();
		} catch (Exception e) {
			assertTrue(host3.getOwner().getChildren().size() == 3);
			checkVerifier(host3.getOwner(), HierarchyVerifier.class);
			assertTrue(host4.getOwner().getChildren().size() == 0);
		}
		btn7.click();
		assertTrue(host3.getOwner().getChildren().size() == 0);
		checkVerifier(host3.getOwner(), NullShadowRoot.class, HierarchyVerifier.class);
		try {
			btn5.click(); //shouldn't catch exception when host has set to null
			assertTrue(host4.getOwner().getChildren().size() == 3);
			checkVerifier(host4.getOwner(), HierarchyVerifier.class);
		} catch (Exception e) {
			fail();
		}
		btn6.click();
		assertTrue(host4.getOwner().getChildren().size() == 3);
		checkVerifier(host4.getOwner(), HierarchyVerifier.class);
	}

	@Test
	public void testInsertChildren() {
		DesktopAgent desktop = connect(getTestURL("F80-ZK-2838Collection.zul"));
		ComponentAgent btn6 = desktop.query("#btn6");
		ComponentAgent btn8 = desktop.query("#btn8");

		ComponentAgent host3 = desktop.query("#host3");
		checkVerifier(host3.getOwner(), HierarchyVerifier.class);
		btn8.click();
		checkVerifier(host3.getOwner(), HierarchyVerifier.class);
		btn8.click();
		checkVerifier(host3.getOwner(), HierarchyVerifier.class);
		btn6.click();
		checkVerifier(host3.getOwner(), HierarchyVerifier.class);
	}

	@Test
	public void testTemplateURI() {
		DesktopAgent desktop = connect(getTestURL("F80-ZK-2838Collection.zul"));
		ComponentAgent btn7 = desktop.query("#btn7");
		ComponentAgent btn9 = desktop.query("#btn9");
		ComponentAgent btn12 = desktop.query("#btn12");

		ComponentAgent host3 = desktop.query("#host3");
		checkVerifier(host3.getOwner(), HierarchyVerifier.class);
		btn7.click();
		checkVerifier(host3.getOwner(), HierarchyVerifier.class);
		btn12.click();
		checkVerifier(host3.getOwner(), HierarchyVerifier.class);
		btn9.click();
		checkVerifier(host3.getOwner(), HierarchyVerifier.class);
		btn12.click();
		checkVerifier(host3.getOwner(), HierarchyVerifier.class);
		btn9.click();
		checkVerifier(host3.getOwner(), HierarchyVerifier.class);
	}
}
