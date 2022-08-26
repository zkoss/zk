/* B95_ZK_4613Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 07 15:01:19 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class B95_ZK_4613Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		Label el1 = desktop.query("#el1").as(Label.class);
		Label el2 = desktop.query("#el2").as(Label.class);
		Label bind1 = desktop.query("#bind1").as(Label.class);
		Label bind2 = desktop.query("#bind2").as(Label.class);

		Assertions.assertEquals("StaticField", el1.getValue());
		Assertions.assertEquals("StaticField", bind1.getValue());
		Assertions.assertEquals("StaticMethod", el2.getValue());
		Assertions.assertEquals("StaticMethod", bind2.getValue());
	}
}
