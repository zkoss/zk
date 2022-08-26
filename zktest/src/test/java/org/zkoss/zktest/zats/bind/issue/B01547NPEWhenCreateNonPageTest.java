package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B01547NPEWhenCreateNonPageTest extends ZATSTestCase{
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent btn1 = desktop.query("#btn1");
		btn1.click();
		assertEquals("A", desktop.query("#cnt #win #lb").as(Label.class).getValue());
	}

	@Test
	public void testB() {
		DesktopAgent desktop = connect();
		ComponentAgent btn2 = desktop.query("#btn2");
		btn2.click();
		assertEquals("A", desktop.query("#cnt #win #lb").as(Label.class).getValue());
	}
}
