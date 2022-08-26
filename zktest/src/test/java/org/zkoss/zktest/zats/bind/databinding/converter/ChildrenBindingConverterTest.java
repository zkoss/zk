/* ConverterTest.java
	Purpose:

	Description:

	History:
		Mon May 10 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
public class ChildrenBindingConverterTest extends ZATSTestCase {

	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		//[Step 1]
		ComponentAgent container1 = desktopAgent.query("#container1");
		ComponentAgent container2 = desktopAgent.query("#container2");
		assertEquals(7, container1.getChildren().size());
		assertEquals(7, container2.getChildren().size());
		ComponentAgent button = desktopAgent.query("button");
		button.click();
		assertEquals(7, container1.getChildren().size());
		assertEquals(8, container2.getChildren().size());
		button.click();
		assertEquals(7, container1.getChildren().size());
		assertEquals(9, container2.getChildren().size());
		button.click();
		assertEquals(7, container1.getChildren().size());
		assertEquals(10, container2.getChildren().size());
	}
}
