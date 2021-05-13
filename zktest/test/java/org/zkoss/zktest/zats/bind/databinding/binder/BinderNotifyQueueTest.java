/* BinderNotifyQueueTest.java
	Purpose:

	Description:

	History:
		Tue May 04 15:42:43 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.binder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class BinderNotifyQueueTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		//[Step 1]
		assertEquals("Outer XYZ", desktopAgent.query("#msg_binder2").as(Label.class).getValue());
		assertEquals("Inner XYZ", desktopAgent.query("#msg_binder3").as(Label.class).getValue());
		assertEquals("Inner XYZ", desktopAgent.query("#msg_binder4").as(Label.class).getValue());
		//[Step 2]
		desktopAgent.query("#outerBtn1").click();
		Label msg_o1 = desktopAgent.query("#msg_o1").as(Label.class);
		Label msg_i1 = desktopAgent.query("#msg_i1").as(Label.class);
		Label msg_i2 = desktopAgent.query("#msg_i2").as(Label.class);
		Label msg_i3 = desktopAgent.query("#msg_i3").as(Label.class);
		Label msg_i4 = desktopAgent.query("#msg_i4").as(Label.class);
		assertEquals("msg1", msg_o1.getValue());
		assertEquals("msg", msg_i1.getValue());
		assertEquals("msg", msg_i2.getValue());
		assertEquals("msg1", msg_i3.getValue());
		assertEquals("msg1", msg_i4.getValue());
		//[Step 3]
		ComponentAgent innerBtn1_1 = desktopAgent.query("#innerBtn1_1");
		innerBtn1_1.click();
		assertEquals("msg1", msg_o1.getValue());
		assertEquals("msg1", msg_i1.getValue());
		assertEquals("msg1", msg_i2.getValue());
		assertEquals("msg1", msg_i3.getValue());
		assertEquals("msg1", msg_i4.getValue());
		//[Step 4] inner1
		ComponentAgent outerBtn2 = desktopAgent.query("#outerBtn2");
		outerBtn2.click();
		innerBtn1_1.click();
		assertEquals("msg1", msg_o1.getValue());
		assertEquals("msg11", msg_i1.getValue());
		assertEquals("msg11", msg_i2.getValue());
		assertEquals("msg1", msg_i3.getValue());
		assertEquals("msg1", msg_i4.getValue());
		//[Step 5] inner1
		desktopAgent.query("#innerBtn1_2").click();
		assertEquals("msg11", msg_o1.getValue());
		assertEquals("msg11", msg_i1.getValue());
		assertEquals("msg11", msg_i2.getValue());
		assertEquals("msg11", msg_i3.getValue());
		assertEquals("msg11", msg_i4.getValue());
		//[Step 4] inner2
		outerBtn2.click();
		desktopAgent.query("#innerBtn2_1").click();
		assertEquals("msg11", msg_o1.getValue());
		assertEquals("msg111", msg_i1.getValue());
		assertEquals("msg111", msg_i2.getValue());
		assertEquals("msg11", msg_i3.getValue());
		assertEquals("msg11", msg_i4.getValue());
		//[Step 5] inner2
		desktopAgent.query("#innerBtn2_2").click();
		assertEquals("msg111", msg_o1.getValue());
		assertEquals("msg111", msg_i1.getValue());
		assertEquals("msg111", msg_i2.getValue());
		assertEquals("msg111", msg_i3.getValue());
		assertEquals("msg111", msg_i4.getValue());
		//[Step 4] inner3
		outerBtn2.click();
		desktopAgent.query("#innerBtn3_1").click();
		assertEquals("msg111", msg_o1.getValue());
		assertEquals("msg1111", msg_i1.getValue());
		assertEquals("msg1111", msg_i2.getValue());
		assertEquals("msg111", msg_i3.getValue());
		assertEquals("msg111", msg_i4.getValue());
		//[Step 5] inner3
		desktopAgent.query("#innerBtn3_2").click();
		assertEquals("msg1111", msg_o1.getValue());
		assertEquals("msg1111", msg_i1.getValue());
		assertEquals("msg1111", msg_i2.getValue());
		assertEquals("msg1111", msg_i3.getValue());
		assertEquals("msg1111", msg_i4.getValue());
		//[Step 4] inner3
		outerBtn2.click();
		desktopAgent.query("#innerBtn4_1").click();
		assertEquals("msg1111", msg_o1.getValue());
		assertEquals("msg11111", msg_i1.getValue());
		assertEquals("msg11111", msg_i2.getValue());
		assertEquals("msg1111", msg_i3.getValue());
		assertEquals("msg1111", msg_i4.getValue());
		//[Step 5] inner3
		desktopAgent.query("#innerBtn4_2").click();
		assertEquals("msg11111", msg_o1.getValue());
		assertEquals("msg11111", msg_i1.getValue());
		assertEquals("msg11111", msg_i2.getValue());
		assertEquals("msg11111", msg_i3.getValue());
		assertEquals("msg11111", msg_i4.getValue());
	}
}
