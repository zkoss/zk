package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class B01344DeletingEntryTest extends ZATSTestCase{
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		for (int i = 10; i > 0; i--) {
			assertEquals(i + "", desktop.query("#lb1").as(Label.class).getValue());
			desktop.query("#btn1").click();
		}
		assertEquals("0", desktop.query("#lb1").as(Label.class).getValue());
	}
}
