/* B80_ZK_3084Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Feb 24 14:12:46 CST 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
public class B80_ZK_3084Test extends ZATSTestCase {
	@Test
	public void test() {
		try {
			DesktopAgent desktop = connect();
			List<ComponentAgent> buttons = desktop.queryAll("button");
			buttons.get(0).click();
			buttons.get(1).click();
			buttons.get(2).click();
		} catch (Exception e) {
			fail();
		}
	}
}
