/* Va12Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class Va12Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();

		ComponentAgent quantityABoxAgent = desktopAgent.query("#quantityABox");
		ComponentAgent quantityBBoxAgent = desktopAgent.query("#quantityBBox");
		ComponentAgent offBoxAgent = desktopAgent.query("#offBox");
		ComponentAgent computeButton = desktopAgent.query("#computeButton");
		Label total = desktopAgent.query("#total").as(Label.class);
		Label subtotalA = desktopAgent.query("#subtotalA").as(Label.class);
		Label subtotalB = desktopAgent.query("#subtotalB").as(Label.class);

		computeButton.click();
		assertEquals("30", total.getValue());

		quantityABoxAgent.input(10);
		assertEquals("100", subtotalA.getValue());

		quantityBBoxAgent.input(11);
		assertEquals("20", subtotalB.getValue());

		quantityBBoxAgent.input(10);
		assertEquals("200", subtotalB.getValue());

		computeButton.click();
		assertEquals("300", total.getValue());

		offBoxAgent.type("90");
		computeButton.click();
		assertEquals("270", total.getValue());
	}
}
