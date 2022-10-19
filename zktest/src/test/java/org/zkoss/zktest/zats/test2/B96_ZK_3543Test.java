/* B96_ZK_3543Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 28 17:45:21 CST 2022, Created by jameschu

Copyright (C) 2022 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Div;
import org.zkoss.zuti.zul.Apply;

/**
 * @author jameschu
 */
public class B96_ZK_3543Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		Div rootDiv = desktop.query("#root").as(Div.class);
		List<Component> shadows = Selectors.find(rootDiv, "::shadow");
		assertEquals(4, shadows.size());
		List<Component> innerApply = Selectors.find(rootDiv, "::shadow#pageContent");
		assertEquals(1, innerApply.size());
		Assert.assertTrue(innerApply.get(0).getClass().equals(Apply.class));
		Assert.assertTrue(innerApply.get(0).getId().equals("pageContent"));
	}
}