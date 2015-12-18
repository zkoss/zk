/** F2Test.java.

	Purpose:
		
	Description:
		
	History:
		2:16:25 PM Dec 31, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.form;

import static org.junit.Assert.*;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 *
 */
public class F2Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent quantityABox = desktop.query("#quantityABox");
		ComponentAgent subtotalA = desktop.query("#subtotalA");
		ComponentAgent quantityBBox = desktop.query("#quantityBBox");
		ComponentAgent subtotalB = desktop.query("#subtotalB");
		ComponentAgent computeButton = desktop.query("#computeButton");
		ComponentAgent total = desktop.query("#total");
		ComponentAgent offBox = desktop.query("#offBox");
		
		quantityABox.type("5");
		quantityBBox.type("5");
		computeButton.click();

		assertEquals("50", subtotalA.as(Label.class).getValue());
		assertEquals("100", subtotalB.as(Label.class).getValue());
		assertEquals("150", total.as(Label.class).getValue());
		
		quantityABox.type("11");
		computeButton.click();
		assertEquals("50", subtotalA.as(Label.class).getValue());
		assertEquals("100", subtotalB.as(Label.class).getValue());
		assertEquals("150", total.as(Label.class).getValue());

		quantityABox.type("5");
		offBox.getLastChild().select();
		computeButton.click();
		assertEquals("50", subtotalA.as(Label.class).getValue());
		assertEquals("100", subtotalB.as(Label.class).getValue());
		assertEquals("75", total.as(Label.class).getValue());
		
	}
}
