/* ConverterTest.java
	Purpose:

	Description:

	History:
		Mon May 10 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.converter;

import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

/**
 * @author jameschu
 */
public class ConverterTest extends ZATSTestCase {
	@ClassRule
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/bind/databinding/converter/converter-zk.xml");

	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		//[Step 1]
		Label msg1 = desktopAgent.query("#msg1").as(Label.class);
		ComponentAgent tb1_1Agent = desktopAgent.query("#tb1_1");
		ComponentAgent tb1_2Agent = desktopAgent.query("#tb1_2");
		boolean exceptionOccur = false;
		try {
			tb1_1Agent.input(123);
		} catch (Exception e) {
			exceptionOccur = true;
		}
		assertTrue("Should found a ParseException", exceptionOccur);
		exceptionOccur = false;
		tb1_1Agent.input("2021/05/10");
		Textbox tb1_1 = tb1_1Agent.as(Textbox.class);
		Textbox tb1_2 = tb1_2Agent.as(Textbox.class);
		assertEquals("2021/05/10", msg1.getValue());
		assertEquals("2021/05/10", tb1_1.getValue());
		assertEquals("2021/05/10", tb1_2.getValue());
		try {
			tb1_2Agent.input(321);
		} catch (Exception e) {
			exceptionOccur = true;
		}
		assertTrue("Should found a ParseException", exceptionOccur);
		exceptionOccur = false;
		tb1_2Agent.input("2021/05/09");
		assertEquals("2021/05/09", msg1.getValue());
		assertEquals("2021/05/09", tb1_1.getValue());
		assertEquals("2021/05/09", tb1_2.getValue());
		//[Step 2]
		Label msg2_1 = desktopAgent.query("#msg2_1").as(Label.class);
		Label msg2_2 = desktopAgent.query("#msg2_2").as(Label.class);
		Label msg2_3 = desktopAgent.query("#msg2_3").as(Label.class);
		ComponentAgent tb2_1Agent = desktopAgent.query("#tb2_1");
		ComponentAgent tb2_2Agent = desktopAgent.query("#tb2_2");
		tb2_1Agent.input("1233");
		Textbox tb2_1 = tb2_1Agent.as(Textbox.class);
		Textbox tb2_2 = tb2_2Agent.as(Textbox.class);
		assertEquals("1233", msg2_1.getValue());
		assertEquals("Foo - 1233", msg2_2.getValue());
		assertEquals("Foo - 1233", tb2_1.getValue());
		assertEquals("1233 - Bar", msg2_3.getValue());
		assertEquals("1233 - Bar", tb2_2.getValue());
		tb2_1Agent.input("3211");
		assertEquals("3211", msg2_1.getValue());
		assertEquals("Foo - 3211", msg2_2.getValue());
		assertEquals("Foo - 3211", tb2_1.getValue());
		assertEquals("3211 - Bar", msg2_3.getValue());
		assertEquals("3211 - Bar", tb2_2.getValue());
		//[Step 3]
		Label msg3_1 = desktopAgent.query("#msg3_1").as(Label.class);
		Label msg3_2 = desktopAgent.query("#msg3_2").as(Label.class);
		Label msg3_3 = desktopAgent.query("#msg3_3").as(Label.class);
		ComponentAgent tb3_1Agent = desktopAgent.query("#tb3_1");
		ComponentAgent tb3_2Agent = desktopAgent.query("#tb3_2");
		ComponentAgent tb3_3Agent = desktopAgent.query("#tb3_3");
		tb3_1Agent.input("-1");
		assertEquals("-1.00", msg3_1.getValue());
		assertEquals("-1.00", tb3_1Agent.as(Textbox.class).getValue());

		tb3_2Agent.input("2021/05/01");
		assertEquals("2021/05/01", msg3_2.getValue());
		assertEquals("2021/05/01", tb3_2Agent.as(Textbox.class).getValue());

		tb3_3Agent.input("112233");
		assertEquals("112233", msg3_3.getValue());
		assertEquals("112233", tb3_3Agent.as(Textbox.class).getValue());

	}
}
