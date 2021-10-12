package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class F01026BindCustomAttrTest extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent l1 = desktop.query("#l1");
		ComponentAgent t2 = desktop.query("#t2");
		ComponentAgent t3 = desktop.query("#t3");
		ComponentAgent msg1 = desktop.query("#msg1");
		ComponentAgent msg2 = desktop.query("#msg2");
		ComponentAgent test1 = desktop.query("#test1");
		ComponentAgent test2 = desktop.query("#test2");
		
		
		assertEquals("A", l1.as(Label.class).getValue());
		assertEquals("B", t2.as(Label.class).getValue());
		assertEquals("C", t3.as(Label.class).getValue());
		assertEquals("", msg1.as(Label.class).getValue());
		assertEquals("", msg2.as(Label.class).getValue());
		
		test1.click();
		assertEquals("A", l1.as(Label.class).getValue());
		assertEquals("y", t2.as(Label.class).getValue());
		assertEquals("z", t3.as(Label.class).getValue());
		assertEquals("value1:A,value2:B,value3:C", msg1.as(Label.class).getValue());
		assertEquals("", msg2.as(Label.class).getValue());
		
		test2.click();
		assertEquals("A", l1.as(Label.class).getValue());
		assertEquals("y", t2.as(Label.class).getValue());
		assertEquals("z", t3.as(Label.class).getValue());
		assertEquals("value1:A,value2:B,value3:C", msg1.as(Label.class).getValue());
		assertEquals("value1:A,value2:y,value3:z", msg2.as(Label.class).getValue());
		
	}
}
