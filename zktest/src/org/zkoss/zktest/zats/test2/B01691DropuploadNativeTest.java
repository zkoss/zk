package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zats.mimic.operation.MultipleSelectAgent;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;

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
