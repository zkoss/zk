/* B95_ZK_3563Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 04 12:46:50 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class B95_ZK_3563Test extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect();
		ComponentAgent saveBtn = desktop.query("#saveBtn");
		saveBtn.click();
		saveBtn.click();
		saveBtn.click();
		Label label2 = desktop.query("#l2").as(Label.class);
		Assert.assertEquals(desktop.query("#l1").as(Label.class).getValue(), label2.getValue());
		desktop.query("#serializeBtn").click();
		saveBtn.click();
		saveBtn.click();
		saveBtn.click();
		Assert.assertEquals(desktop.query("#l1").as(Label.class).getValue(), label2.getValue());
	}
}
