/* EL3Test.java
	Purpose:

	Description:

	History:
		Tue May 04 15:42:43 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.el;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class MethodsTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		//[Step 1]
		assertEquals("my-test-valuepostfix", desktopAgent.query("#result1").as(Label.class).getValue());
		//[Step 2]
		assertEquals("A-my-test-value", desktopAgent.query("#result2").as(Label.class).getValue());
		//[Step 3]
		desktopAgent.query("#ib3").input(7);
		assertEquals("$07 per person", desktopAgent.query("#result3").as(Label.class).getValue());
		//[Step 4]
		assertEquals("array v1", desktopAgent.query("#result4").as(Label.class).getValue());
		//[Step 5]
		Label result5Label = desktopAgent.query("#result5").as(Label.class);
		assertEquals("7", result5Label.getValue());
		desktopAgent.query("#ib5_1").input(10);
		assertEquals("10", result5Label.getValue());
		desktopAgent.query("#ib5_2").input(20);
		assertEquals("20", result5Label.getValue());
		//[Step 6]
		Label result6_1Label = desktopAgent.query("#result6_1").as(Label.class);
		Label result6_2Label = desktopAgent.query("#result6_2").as(Label.class);
		Label result6_3Label = desktopAgent.query("#result6_3").as(Label.class);
		assertEquals(">>null", result6_1Label.getValue());
		assertEquals("not 123", result6_2Label.getValue());
		assertEquals("not 123", result6_3Label.getValue());
		desktopAgent.query("#tb6").input("123");
		assertEquals(">>123", result6_1Label.getValue());
		assertEquals("picture is 123", result6_2Label.getValue());
		assertEquals("picture is 123", result6_3Label.getValue());
		//[Step 7]
		ComponentAgent tb12 = desktopAgent.query("#tb7");
		Label result7_1Label = desktopAgent.query("#result7_1").as(Label.class);
		Label result7_2Label = desktopAgent.query("#result7_2").as(Label.class);
		Label result7_3Label = desktopAgent.query("#result7_3").as(Label.class);
		Label result7_4Label = desktopAgent.query("#result7_4").as(Label.class);
		assertEquals("false", result7_1Label.getValue());
		assertEquals("true", result7_2Label.getValue());
		assertEquals("false", result7_3Label.getValue());
		assertEquals("true", result7_4Label.getValue());
		tb12.input("16");
		assertEquals("true", result7_1Label.getValue());
		assertEquals("true", result7_2Label.getValue());
		assertEquals("false", result7_3Label.getValue());
		assertEquals("false", result7_4Label.getValue());
		tb12.input("19");
		assertEquals("false", result7_1Label.getValue());
		assertEquals("false", result7_2Label.getValue());
		assertEquals("true", result7_3Label.getValue());
		assertEquals("true", result7_4Label.getValue());
	}
}
