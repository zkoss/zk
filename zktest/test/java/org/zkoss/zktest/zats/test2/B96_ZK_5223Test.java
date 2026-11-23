/* B96_ZK_5223Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Sep 29 16:13:11 CST 2022, Created by jameschu

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class B96_ZK_5223Test extends ZATSTestCase {
	@Test
	public void test() throws Exception {
		DesktopAgent desktop = connect();
		verifyResult(desktop);

		ComponentAgent button = desktop.query("button");
		button.click();
		verifyResult(desktop);

		button.click();
		verifyResult(desktop);
	}

	private void verifyResult(DesktopAgent desktop) {
		List<ComponentAgent> content = desktop.queryAll("#contentDiv div");
		String firstDivLabel = content.get(0).query("label").as(Label.class).getValue();
		String secondDivLabel = content.get(1).query("label").as(Label.class).getValue();
		String thirdDivLabel = content.get(2).query("label").as(Label.class).getValue();
		assertEquals("1) some control", firstDivLabel.trim());
		assertEquals("2)", secondDivLabel.trim());
		assertTrue(thirdDivLabel.trim().contains("3) show"));
	}
}