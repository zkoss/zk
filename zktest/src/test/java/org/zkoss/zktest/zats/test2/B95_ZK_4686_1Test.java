/* B95_ZK_4686_1Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 24 10:25:45 AM CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class B95_ZK_4686_1Test extends ZATSTestCase {
	@Test
	public void test1() throws Exception {
		DesktopAgent desktop = connect();
		Label result = desktop.query("#template").as(Label.class);
		assertEquals("default", result.getValue());
		ComponentAgent btn1 = desktop.query("#displayBtn1");
		btn1.click();
		assertEquals("form1", result.getValue());
		ComponentAgent btn2 = desktop.query("#displayBtn2");
		btn2.click();
		assertEquals("form2", result.getValue());
		btn1.click();
		assertEquals("form1", result.getValue());
		btn2.click();
		assertEquals("form2", result.getValue());
	}
}
