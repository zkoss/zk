package org.zkoss.zktest.zats;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zats.mimic.operation.InputAgent;
import org.zkoss.zats.mimic.operation.SelectAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;

public class B00634Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent t11 = desktop.query("#t11");
		ComponentAgent l11 = desktop.query("#l11");
		ComponentAgent l12 = desktop.query("#l12");
		assertEquals("A", l11.as(Label.class).getValue());
		assertEquals("B", l12.as(Label.class).getValue());
		
		t11.as(InputAgent.class).type("PP");
		ComponentAgent btn = desktop.query("button");
		ComponentAgent msg1 = desktop.query("#msg1");
		ComponentAgent msg2 = desktop.query("#msg2");
		
		btn.click();
		assertEquals("A", l11.as(Label.class).getValue());
		assertEquals("B", l12.as(Label.class).getValue());
		assertEquals("value 1 has to be XX or ZZ", msg1.as(Label.class).getValue());
		assertEquals("value 2 has to be YY or ZZ", msg2.as(Label.class).getValue());
		
		t11.type("XX");
		btn.click();
		assertEquals("A", l11.as(Label.class).getValue());
		assertEquals("B", l12.as(Label.class).getValue());
		assertEquals("", msg1.as(Label.class).getValue());
		assertEquals("value 2 has to be YY or ZZ", msg2.as(Label.class).getValue());
		
		t11.type("YY");
		btn.click();
		assertEquals("A", l11.as(Label.class).getValue());
		assertEquals("B", l12.as(Label.class).getValue());
		assertEquals("value 1 has to be XX or ZZ", msg1.as(Label.class).getValue());
		assertEquals("", msg2.as(Label.class).getValue());
		
		t11.type("ZZ");
		btn.click();
		assertEquals("ZZ", l11.as(Label.class).getValue());
		assertEquals("ZZ", l12.as(Label.class).getValue());
		assertEquals("", msg1.as(Label.class).getValue());
		assertEquals("", msg2.as(Label.class).getValue());
	}
}
