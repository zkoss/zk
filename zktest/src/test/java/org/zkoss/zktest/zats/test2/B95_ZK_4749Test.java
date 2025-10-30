/* B95_ZK_4749Test.java

	Purpose:

	Description:

	History:
		Fri Dec 18 10:50:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class B95_ZK_4749Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		desktop.query("#prefillBtn").click();
		Assertions.assertNotEquals("", desktop.query("#l1").as(Label.class).getValue().trim());
		Assertions.assertNotEquals("", desktop.query("#l2").as(Label.class).getValue().trim());
		Assertions.assertNotEquals("", desktop.query("#l2_1").as(Label.class).getValue().trim());
		Assertions.assertNotEquals("", desktop.query("#l3").as(Label.class).getValue().trim());
	}
}
