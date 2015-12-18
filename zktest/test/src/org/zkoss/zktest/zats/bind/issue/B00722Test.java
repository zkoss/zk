package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class B00722Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent l11 = desktop.query("#l11");
		ComponentAgent t21 = desktop.query("#t21");
		ComponentAgent m21 = desktop.query("#m21");
		ComponentAgent cmd1 = desktop.query("#cmd1");
		ComponentAgent cmd2 = desktop.query("#cmd2");
		
		assertEquals("abc", l11.as(Label.class).getValue());
		assertEquals("abc", t21.as(Textbox.class).getValue());
		assertEquals("", m21.as(Label.class).getValue());
		
		t21.type("efg");
		assertEquals("abc", l11.as(Label.class).getValue());
		assertEquals("efg", t21.as(Textbox.class).getValue());
		assertEquals("the value has to be 'abc' or 'ABC'", m21.as(Label.class).getValue());
		
		cmd1.click();
		assertEquals("abc", l11.as(Label.class).getValue());
		assertEquals("efg", t21.as(Textbox.class).getValue());
		assertEquals("the value has to be 'abc' or 'ABC'", m21.as(Label.class).getValue());
		
		t21.type("ABC");
		assertEquals("abc", l11.as(Label.class).getValue());
		assertEquals("ABC", t21.as(Textbox.class).getValue());
		assertEquals("", m21.as(Label.class).getValue());
		
		cmd1.click();
		assertEquals("ABC:saved", l11.as(Label.class).getValue());
		assertEquals("ABC", t21.as(Textbox.class).getValue());
		assertEquals("", m21.as(Label.class).getValue());
		
		t21.type("kkk");
		assertEquals("ABC:saved", l11.as(Label.class).getValue());
		assertEquals("kkk", t21.as(Textbox.class).getValue());
		assertEquals("the value has to be 'abc' or 'ABC'", m21.as(Label.class).getValue());
		
		cmd2.click();
		assertEquals("ABC:saved", l11.as(Label.class).getValue());
		assertEquals("ABC:saved", t21.as(Textbox.class).getValue());
		assertEquals("", m21.as(Label.class).getValue());
	}
}
