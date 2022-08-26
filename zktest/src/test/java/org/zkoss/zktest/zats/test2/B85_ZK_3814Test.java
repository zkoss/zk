/* B85_ZK_3814Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 07 12:15:45 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class B85_ZK_3814Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		Label fullName = desktop.query("#fullname").as(Label.class);

		assertEquals("Hawk Chen", fullName.getValue());
	}
}
