/* DependsOnTest.java

	Purpose:
		
	Description:
		
	History:
		Wed May 05 11:52:43 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.viewmodel.notification;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class DependsOnTest extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect("/bind/viewmodel/notification/dependson.zul");
		final Label fullname = desktop.query("#fullname").as(Label.class);
		final Label fn = desktop.query("#firstname").as(Label.class);
		final Label ln = desktop.query("#lastname").as(Label.class);
		Assertions.assertEquals("John Smith", fullname.getValue());
		Assertions.assertEquals("John", fn.getValue());
		Assertions.assertEquals("Smith", ln.getValue());

		desktop.query("#fn").type("Tom");
		desktop.query("#ln").type("Riddle");
		Assertions.assertEquals("Tom Riddle", fullname.getValue());
		Assertions.assertEquals("Tom", fn.getValue());
		Assertions.assertEquals("Riddle", ln.getValue());
	}
}
