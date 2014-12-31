package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class B01005BeanValidatorPathTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent t1 = desktop.query("#t1");
		ComponentAgent t2 = desktop.query("#t2");
		ComponentAgent l1 = desktop.query("#l1");
		ComponentAgent msg1 = desktop.query("#msg1");
		ComponentAgent msg2 = desktop.query("#msg2");
		ComponentAgent update = desktop.query("#update");
		ComponentAgent msg = desktop.query("#msg");
		
		assertEquals("A", t1.as(Textbox.class).getValue());
		
		t1.type("Aa");
		assertEquals("min length is 3", msg1.as(Label.class).getValue());
		assertEquals("A", l1.as(Label.class).getValue());
		
		t1.type("Aab");
		assertEquals("", msg1.as(Label.class).getValue());
		assertEquals("Aab", l1.as(Label.class).getValue());
		assertEquals("A", t2.as(Textbox.class).getValue());
		
		t2.type("Ab");
		assertEquals("min length is 3", msg2.as(Label.class).getValue());
		assertEquals("Aab", l1.as(Label.class).getValue());
		
		t2.type("Abc");
		assertEquals("", msg2.as(Label.class).getValue());
		assertEquals("Aab", l1.as(Label.class).getValue());
		
		update.click();
		assertEquals("Abc", t1.as(Textbox.class).getValue());
		assertEquals("Abc", l1.as(Label.class).getValue());
		assertEquals("update value1:Abc", msg.as(Label.class).getValue());
	}
}
