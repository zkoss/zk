package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class B01157CorrectConverterNameTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent lb1 = desktop.query("#lb1");
		ComponentAgent lb2 = desktop.query("#lb2");

		assertEquals("12,345.68", lb1.as(Label.class).getValue());
		String[] date = lb2.as(Label.class).getValue().split("/");
		assertEquals(3, date.length);
		assertEquals(4, date[0].length());
		assertEquals(2, date[1].length());
		assertEquals(2, date[2].length());
		
		
	}
}
