package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class B02083FormRefTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent tb1 = desktop.query("#tb1");
		ComponentAgent tb2 = desktop.query("#tb2");
		ComponentAgent tb3 = desktop.query("#tb3");
		ComponentAgent lb1 = desktop.query("#lb1");
		ComponentAgent lb2 = desktop.query("#lb2");
		ComponentAgent lb3 = desktop.query("#lb3");
		ComponentAgent save = desktop.query("#save");
		
		assertEquals("AAA", tb1.as(Textbox.class).getValue());
		assertEquals("AAA1", tb2.as(Textbox.class).getValue());
		assertEquals("AAA2", tb3.as(Textbox.class).getValue());
		assertEquals("AAA", lb1.as(Label.class).getValue());
		assertEquals("AAA1", lb2.as(Label.class).getValue());
		assertEquals("AAA2", lb3.as(Label.class).getValue());
		
		tb1.type("fu");
		assertEquals("fu", tb1.as(Textbox.class).getValue());
		assertEquals("AAA1", tb2.as(Textbox.class).getValue());
		assertEquals("AAA2", tb3.as(Textbox.class).getValue());
		assertEquals("AAA", lb1.as(Label.class).getValue());
		assertEquals("AAA1", lb2.as(Label.class).getValue());
		assertEquals("AAA2", lb3.as(Label.class).getValue());
		
		tb2.type("fu1");
		assertEquals("fu", tb1.as(Textbox.class).getValue());
		assertEquals("fu1", tb2.as(Textbox.class).getValue());
		assertEquals("AAA2", tb3.as(Textbox.class).getValue());
		assertEquals("AAA", lb1.as(Label.class).getValue());
		assertEquals("fu1", lb2.as(Label.class).getValue());
		assertEquals("AAA2", lb3.as(Label.class).getValue());
		
		tb3.type("fu2");
		assertEquals("fu", tb1.as(Textbox.class).getValue());
		assertEquals("fu1", tb2.as(Textbox.class).getValue());
		assertEquals("fu2", tb3.as(Textbox.class).getValue());
		assertEquals("AAA", lb1.as(Label.class).getValue());
		assertEquals("fu1", lb2.as(Label.class).getValue());
		assertEquals("AAA2", lb3.as(Label.class).getValue());
		
		save.click();
		assertEquals("fu", tb1.as(Textbox.class).getValue());
		assertEquals("fu1", tb2.as(Textbox.class).getValue());
		assertEquals("fu2", tb3.as(Textbox.class).getValue());
		assertEquals("fu", lb1.as(Label.class).getValue());
		assertEquals("fu1", lb2.as(Label.class).getValue());
		assertEquals("AAA2", lb3.as(Label.class).getValue());

	}
}
