/* B80_ZK_3066Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 11 14:36:03 CST 2016, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class B80_ZK_3066Test extends ZATSTestCase {

	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent dpLabel = desktop.query("#dp");
		assertEquals("ccc", dpLabel.as(Label.class).getValue());
	}
}
