/* MenuitemTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 12:44:08 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.comp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class MenuitemTest extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect();
		final ComponentAgent menuitem = desktop.query("window menuitem");
		final Label checked = desktop.query("window #checked").as(Label.class);

		menuitem.check(true);
		Assertions.assertEquals("true", checked.getValue());
		menuitem.check(false);
		Assertions.assertEquals("false", checked.getValue());
	}
}
