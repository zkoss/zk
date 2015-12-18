package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B01960BindArrayTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent tb1 = desktop.query("#tb1");
		ComponentAgent lb1 = desktop.query("#lb1");
		ComponentAgent tb2 = desktop.query("#tb2");
		ComponentAgent lb2 = desktop.query("#lb2");
		ComponentAgent tb3 = desktop.query("#tb3");
		ComponentAgent lb3 = desktop.query("#lb3");
		ComponentAgent tb4 = desktop.query("#tb4");
		ComponentAgent lb4 = desktop.query("#lb4");
		
		assertEquals("This", lb1.as(Label.class).getValue());
		assertEquals("is", lb2.as(Label.class).getValue());
		assertEquals("a", lb3.as(Label.class).getValue());
		assertEquals("Test", lb4.as(Label.class).getValue());
		
		tb1.type("yo");
		assertEquals("yo", lb1.as(Label.class).getValue());
		tb2.type("yoo");
		assertEquals("yoo", lb2.as(Label.class).getValue());
		tb3.type("yooo");
		assertEquals("yooo", lb3.as(Label.class).getValue());
		tb4.type("yoooo");
		assertEquals("yoooo", lb4.as(Label.class).getValue());
		
	}
}
