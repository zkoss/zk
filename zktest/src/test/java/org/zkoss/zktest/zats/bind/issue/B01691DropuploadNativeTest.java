package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B01691DropuploadNativeTest extends ZATSTestCase{
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent lab1 = desktop.query("#lab1");
		ComponentAgent lab2 = desktop.query("#lab2");
		ComponentAgent btn1 = desktop.query("#btn1");
		ComponentAgent btn2 = desktop.query("#btn2");
		
		btn1.click();
		assertEquals("true", lab1.as(Label.class).getValue());
		btn2.click();
		assertEquals("native is true", lab2.as(Label.class).getValue());
		
		btn1.click();
		assertEquals("false", lab1.as(Label.class).getValue());
		btn2.click();
		assertEquals("native is false", lab2.as(Label.class).getValue());
	}
}
